package com.itau.desafio.vendas.audit.application.port.in;

import com.itau.desafio.vendas.audit.domain.model.AuditEvent;

/**
 * Porta de Entrada que define o caso de uso para processar
 * um evento de auditoria de mudança de dados.
 */
public interface ProcessAuditEventUseCase {

    /**
     * Executa o processamento de um evento de auditoria.
     *
     * @param auditEvent O objeto contendo as informações da mudança a ser
     *                   processada.
     */
    void process(AuditEvent auditEvent);
}