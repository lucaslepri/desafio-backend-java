package com.itau.desafio.vendas.domain.port.out;

import com.itau.desafio.vendas.domain.model.CpfStatus;

/**
 * Define o contrato para diferentes estratégias de validação de CPF.
 * Permite que a lógica de negócio seja desacoplada da implementação
 * específica da validação (ex: externa, interna, mockada).
 */
@FunctionalInterface
public interface CpfValidationStrategy {
    CpfStatus validateCpf(String cpf);
}