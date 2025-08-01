package com.itau.desafio.vendas.audit.infrastructure.adapter.out.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.desafio.vendas.audit.domain.model.AuditEvent;
import com.itau.desafio.vendas.audit.domain.port.out.AuditEventExporterPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@Profile("file")
public class FileSystemAuditEventExporterAdapter implements AuditEventExporterPort {

    @Value("${audit.export.directory:./audit-logs}")
    private String exportDirectoryPath;

    private Path exportDirectory;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        this.exportDirectory = Paths.get(exportDirectoryPath);
        if (Files.notExists(exportDirectory)) {
            Files.createDirectories(exportDirectory);
            log.info("Diretório de auditoria criado em: {}", exportDirectory.toAbsolutePath());
        }

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void export(AuditEvent auditEvent) {
        log.trace("Exportando evento de auditoria: {}", auditEvent);
        try {
            String fileName = generateFileName(auditEvent);
            Path filePath = this.exportDirectory.resolve(fileName);

            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(auditEvent);

            Files.writeString(filePath, jsonContent, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            log.info("Evento de auditoria exportado com sucesso para o arquivo: {}", filePath.toAbsolutePath());

        } catch (IOException e) {
            log.error("Falha ao exportar evento de auditoria para o sistema de arquivos. Evento: {}", auditEvent, e);
            // -> Aqui certamente precisa de tratamento para além deste ms.
        }
    }

    private String generateFileName(AuditEvent auditEvent) {
        String timestamp = auditEvent.eventTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS"));
        String collection = auditEvent.collectionName();
        String operation = auditEvent.operationType().name();
        String docId = auditEvent.documentKey().get("_id").toString().replaceAll("[^a-zA-Z0-9\\-]", "");

        return String.format("%s-%s-%s-%s.json", timestamp, collection, operation, docId);
    }
}