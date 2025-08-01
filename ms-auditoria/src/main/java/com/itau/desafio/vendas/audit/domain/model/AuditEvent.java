package com.itau.desafio.vendas.audit.domain.model;

import org.bson.Document;
import java.time.LocalDateTime;

/**
 * Representa um evento de auditoria genérico capturado do banco de dados.
 * Este é um Objeto de Valor (Value Object) que encapsula todos os detalhes
 * relevantes de uma mudança de dados, independentemente da coleção.
 *
 * @param operationType  O tipo de operação (CREATED, UPDATED, DELETED, etc.).
 * @param eventTimestamp O momento exato em que a mudança ocorreu (ClusterTime).
 * @param databaseName   O nome do banco de dados onde a mudança ocorreu.
 * @param collectionName O nome da coleção afetada.
 * @param changedBy      O identificador do usuário ou sistema que realizou a
 *                       alteração.
 *                       Extraído do próprio documento (ex: campo
 *                       'lastModifiedBy').
 * @param documentKey    A chave do documento que foi alterado.
 * @param documentBefore O estado completo do documento ANTES da alteração.
 *                       Nulo para operações de inserção (INSERT).
 * @param documentAfter  O estado completo do documento DEPOIS da alteração.
 *                       Nulo para operações de exclusão (DELETE).
 */
public record AuditEvent(
        ChangeType operationType,
        LocalDateTime eventTimestamp,
        String databaseName,
        String collectionName,
        String changedBy,
        Document documentKey,
        Document documentBefore,
        Document documentAfter) {
}