package com.itau.desafio.vendas.domain.model;

/**
 * Representa o estado de validação do CPF de um cliente na Receita Federal.
 */
public enum CpfStatus {
    CPF_ATIVO,

    CPF_INEXISTENTE,

    CPF_BLOQUEADO,

    CPF_CANCELADO,

    PENDENTE_VALIDACAO_RECEITA;
}