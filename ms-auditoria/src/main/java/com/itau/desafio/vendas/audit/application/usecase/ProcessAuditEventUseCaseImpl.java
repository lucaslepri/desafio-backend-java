package com.itau.desafio.vendas.audit.application.usecase;

import io.micrometer.core.instrument.MeterRegistry;
import com.itau.desafio.vendas.audit.application.port.in.ProcessAuditEventUseCase;
import com.itau.desafio.vendas.audit.domain.model.AuditEvent;
import com.itau.desafio.vendas.audit.domain.port.out.AuditEventExporterPort;

public class ProcessAuditEventUseCaseImpl implements ProcessAuditEventUseCase {

    private final AuditEventExporterPort auditEventExporter;
    private final MeterRegistry meterRegistry;

    public ProcessAuditEventUseCaseImpl(AuditEventExporterPort auditEventExporter, MeterRegistry meterRegistry) {
        this.auditEventExporter = auditEventExporter;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void process(AuditEvent auditEvent) {
        Thread.startVirtualThread(() -> {
            meterRegistry.counter(
                    "audit.events.processed",
                    "processor", "changestream",
                    "operation", auditEvent.operationType().name()).increment();

            auditEventExporter.export(auditEvent);
        });
    }
}