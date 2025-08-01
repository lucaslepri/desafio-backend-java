package com.itau.desafio.vendas.infrastructure.adapters.out.rest;

import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementação da estratégia de validação de CPF que representa o provedor
 * "Jato".
 * Para fins de demonstração, este serviço sempre retorna CPF_ATIVO.
 */
@Component
@Qualifier("jatoCpf")
@Slf4j
public class JatoCpfServicesAdapter implements CpfValidationStrategy {

    @Override
    public CpfStatus validateCpf(String cpf) {
        log.debug("Estratégia 'JatoCpfServicesAdapter' em uso. Retornando CPF_ATIVO para o CPF: {}", cpf);
        return CpfStatus.CPF_ATIVO;
    }
}