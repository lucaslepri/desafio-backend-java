package com.itau.desafio.vendas.audit.infrastructure.adapter.in.changestream;

import com.itau.desafio.vendas.audit.application.port.in.ProcessAuditEventUseCase;
import com.itau.desafio.vendas.audit.domain.model.AuditEvent;
import com.itau.desafio.vendas.audit.domain.model.ChangeType;
import com.mongodb.client.MongoChangeStreamCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.FullDocumentBeforeChange;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class MongoChangeStreamListenerAdapter {

    private final MongoTemplate mongoTemplate;
    private final ProcessAuditEventUseCase processAuditEventUseCase;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private MongoChangeStreamCursor<ChangeStreamDocument<Document>> cursor;

    private volatile boolean isRunning = false;
    private final AtomicReference<Exception> lastException = new AtomicReference<>(null);

    public MongoChangeStreamListenerAdapter(MongoTemplate mongoTemplate,
            ProcessAuditEventUseCase processAuditEventUseCase) {
        this.mongoTemplate = mongoTemplate;
        this.processAuditEventUseCase = processAuditEventUseCase;
    }

    @PostConstruct
    private void startListening() {
        log.info("Iniciando o listener do MongoDB Change Stream para todo o banco de dados.");
        executorService.submit(this::listenToChanges);
    }

    // TODO: usar eventos? Reactive Streams?
    private void listenToChanges() {
        cursor = mongoTemplate.getDb().watch()
                .fullDocument(FullDocument.UPDATE_LOOKUP)
                .fullDocumentBeforeChange(FullDocumentBeforeChange.WHEN_AVAILABLE)
                .cursor();

        log.info("Conectado ao Change Stream do banco '{}'. Aguardando por alterações.",
                mongoTemplate.getDb().getName());
        try {
            this.isRunning = true;
            this.lastException.set(null);
            while (cursor.hasNext() && !Thread.currentThread().isInterrupted()) {
                ChangeStreamDocument<Document> event = cursor.next();
                log.trace("Evento do Change Stream recebido: {}", event);

                convertToAuditEvent(event).ifPresent(processAuditEventUseCase::process);
            }
        } catch (Exception e) {
            this.lastException.set(e);
            if (!executorService.isShutdown()) {
                log.error("Erro fatal ao processar o Change Stream do MongoDB. O listener será encerrado.", e);
            }
        } finally {
            this.isRunning = false;
            closeCursor();
        }
    }

    private Optional<AuditEvent> convertToAuditEvent(ChangeStreamDocument<Document> event) {
        log.trace("Convertendo Change Stream Document para AuditEvent: {}", event);
        ChangeType changeType;
        switch (event.getOperationType()) {
            case INSERT:
                changeType = ChangeType.CREATED;
                break;
            case UPDATE:
            case REPLACE:
                changeType = ChangeType.UPDATED;
                break;
            case DELETE:
                changeType = ChangeType.DELETED;
                break;
            default:
                log.warn("Tipo de operação não mapeada recebida do Change Stream: {}", event.getOperationType());
                return Optional.empty();
        }

        Document documentBefore = event.getFullDocumentBeforeChange();
        Document documentAfter = event.getFullDocument();

        String dbName = event.getNamespace().getDatabaseName();
        String collectionName = event.getNamespace().getCollectionName();

        Document documentKey = Document.parse(event.getDocumentKey().toJson());

        String changedBy = extractChangedBy(documentAfter, documentBefore);

        AuditEvent auditEvent = new AuditEvent(
                changeType,
                LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getClusterTime().getTime()), ZoneOffset.UTC),
                dbName,
                collectionName,
                changedBy,
                documentKey,
                documentBefore,
                documentAfter);

        return Optional.of(auditEvent);
    }

    /**
     * Helper para extrair o campo 'lastModifiedBy' do documento.
     * Tenta primeiro no documento 'after', depois no 'before'.
     */
    private String extractChangedBy(Document docAfter, Document docBefore) {
        if (docAfter != null && docAfter.containsKey("lastModifiedBy")) {
            return docAfter.getString("lastModifiedBy");
        }
        if (docBefore != null && docBefore.containsKey("lastModifiedBy")) {
            return docBefore.getString("lastModifiedBy");
        }
        return "system/unknown";
    }

    @PreDestroy
    private void stopListening() {
        log.info("Encerrando o listener do MongoDB Change Stream.");
        executorService.shutdownNow();
        closeCursor();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor service não encerrou graciosamente.");
            }
        } catch (InterruptedException e) {
            log.warn("Thread interrompida durante o encerramento do executor service.");
            Thread.currentThread().interrupt();
        }
        log.info("Listener do Change Stream encerrado.");
    }

    private void closeCursor() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                log.warn("Erro ao fechar o cursor do Change Stream.", e);
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Exception getLastException() {
        return lastException.get();
    }
}