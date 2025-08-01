package com.itau.desafio.vendas.application.port.in;

import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.domain.model.Vehicle;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateProposalRequestUseCase {
    /*
     * Cria uma nova solicitação de proposta com os dados fornecidos.
     * * @param customerId ID do cliente que está fazendo a solicitação.
     * * @param vehicle Veículo para o qual a proposta está sendo solicitada.
     * * @param downPayment Valor de entrada para a proposta.
     * * @return A nova solicitação de proposta criada.
     */
    ProposalRequest createProposalRequest(UUID customerId, Vehicle vehicle, BigDecimal downPayment);
}
