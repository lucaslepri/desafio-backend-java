package com.itau.desafio.vendas.audit.infrastructure.adapter.out.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.desafio.vendas.audit.domain.model.AuditEvent;
import com.itau.desafio.vendas.audit.domain.port.out.AuditEventExporterPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

@Component
@Slf4j
@Primary
public class S3AuditEventExporterAdapter implements AuditEventExporterPort {

    private final Semaphore s3ConcurrencyLimiter = new Semaphore(20);

    private final String bucketName;
    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public S3AuditEventExporterAdapter(S3Client s3Client, @Value("${audit.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("Exportador de Auditoria S3 configurado para o bucket '{}'", bucketName);
    }

    @Override
    public void export(AuditEvent auditEvent) {
        log.trace("Exportando evento de auditoria: {}", auditEvent);
        String fileName = generateFileName(auditEvent);
        try {
            s3ConcurrencyLimiter.acquire();
            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(auditEvent);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));

            log.trace("Evento de auditoria exportado com sucesso para S3: s3://{}/{}", bucketName, fileName);

        } catch (Exception e) {
            log.error("Falha ao exportar evento de auditoria para o S3. Arquivo: {}. Evento: {}", fileName, auditEvent,
                    e);
            // -> Entendo que aqui certamente precisaria, no mundo real, de tratamento para
            // além deste ms.
        }
    }

    private String generateFileName(AuditEvent auditEvent) {
        String timestamp = auditEvent.eventTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmssSSS"));
        String collection = auditEvent.collectionName();
        String operation = auditEvent.operationType().name();
        String docId = auditEvent.documentKey().get("_id").toString().replaceAll("[^a-zA-Z0-9\\-]", "");

        String datePath = auditEvent.eventTimestamp().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return String.format("%s/%s-%s-%s-%s.json", datePath, timestamp, collection, operation, docId);
    }
}