package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.application.port.in.CreateProposalRequestUseCase;
import com.itau.desafio.vendas.domain.exceptions.BusinessRuleException;
import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.domain.model.RequestStatus;
import com.itau.desafio.vendas.domain.model.Vehicle;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import com.itau.desafio.vendas.domain.port.out.ProposalRequestRepositoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateProposalRequestUseCaseImpl implements CreateProposalRequestUseCase {

    private static final int MAX_SOLICITACOES_PENDENTES = 2;
    private final Counter proposalsCreatedCounter;
    private final CustomerRepositoryPort customerRepositoryPort;
    private final ProposalRequestRepositoryPort solicitacaoRepositoryPort;

    public CreateProposalRequestUseCaseImpl(CustomerRepositoryPort customerRepositoryPort,
            ProposalRequestRepositoryPort solicitacaoRepositoryPort,
            MeterRegistry meterRegistry) {
        this.customerRepositoryPort = customerRepositoryPort;
        this.solicitacaoRepositoryPort = solicitacaoRepositoryPort;
        this.proposalsCreatedCounter = Counter.builder("proposals.created")
                .description("Número de propostas de financiamento criadas")
                .register(meterRegistry);
    }

    @Override
    public ProposalRequest createProposalRequest(UUID customerId, Vehicle vehicle, BigDecimal downPayment) {
        checkCustomerExists(customerId);
        checkPendingRequestsLimit(customerId);

        ProposalRequest novaProposalRequest = ProposalRequest.create(customerId, vehicle, downPayment, "system");

        this.proposalsCreatedCounter.increment();

        return solicitacaoRepositoryPort.save(novaProposalRequest);
    }

    private void checkCustomerExists(UUID customerId) {
        customerRepositoryPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer com ID " + customerId + " não encontrado."));
    }

    private void checkPendingRequestsLimit(UUID customerId) {
        long count = solicitacaoRepositoryPort.countByCustomerIdAndStatus(customerId, RequestStatus.PENDING_ANALYSIS);
        if (count >= MAX_SOLICITACOES_PENDENTES) {
            throw new BusinessRuleException(
                    "Limite de " + MAX_SOLICITACOES_PENDENTES + " solicitações pendentes atingido para este customer.");
        }
    }
}