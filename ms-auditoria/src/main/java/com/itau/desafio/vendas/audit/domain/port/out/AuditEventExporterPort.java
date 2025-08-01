package com.itau.desafio.vendas.audit.domain.port.out;

import com.itau.desafio.vendas.audit.domain.model.AuditEvent;

public interface AuditEventExporterPort {
    void export(AuditEvent auditEvent);
}