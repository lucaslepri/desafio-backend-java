package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.exceptions.BusinessRuleException;
import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.domain.model.RequestStatus;
import com.itau.desafio.vendas.domain.model.Vehicle;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import com.itau.desafio.vendas.domain.port.out.ProposalRequestRepositoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProposalRequestUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @Mock
    private ProposalRequestRepositoryPort proposalRequestRepositoryPort;

    private MeterRegistry meterRegistry;

    private CreateProposalRequestUseCaseImpl createProposalRequestUseCase;

    private UUID customerId;
    private Vehicle vehicle;
    private BigDecimal downPayment;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        createProposalRequestUseCase = new CreateProposalRequestUseCaseImpl(
                customerRepositoryPort,
                proposalRequestRepositoryPort,
                meterRegistry
        );

        customerId = UUID.randomUUID();
        vehicle = Vehicle.create("Test Car", new BigDecimal("50000"), 2022, "system");
        downPayment = new BigDecimal("10000");
    }

    @Test
    @DisplayName("Deve criar uma proposta com sucesso quando todos os dados são válidos")
    void shouldCreateProposalRequestSuccessfully_whenDataIsValid() {
        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(proposalRequestRepositoryPort.countByCustomerIdAndStatus(customerId, RequestStatus.PENDING_ANALYSIS)).thenReturn(0L);
        when(proposalRequestRepositoryPort.save(any(ProposalRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProposalRequest result = createProposalRequestUseCase.createProposalRequest(customerId, vehicle, downPayment);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(RequestStatus.PENDING_ANALYSIS, result.getStatus());
        verify(customerRepositoryPort, times(1)).findById(customerId);
        verify(proposalRequestRepositoryPort, times(1)).countByCustomerIdAndStatus(customerId, RequestStatus.PENDING_ANALYSIS);
        verify(proposalRequestRepositoryPort, times(1)).save(any(ProposalRequest.class));

        Counter counter = meterRegistry.find("proposals.created").counter();
        assertNotNull(counter);
        assertEquals(1, counter.count());
    }

    @Test
    @DisplayName("Deve lançar CustomerNotFoundException quando o cliente não existe")
    void shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                createProposalRequestUseCase.createProposalRequest(customerId, vehicle, downPayment));

        verify(proposalRequestRepositoryPort, never()).countByCustomerIdAndStatus(any(), any());
        verify(proposalRequestRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o limite de propostas pendentes é atingido")
    void shouldThrowBusinessRuleException_whenPendingProposalsLimitIsReached() {
        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(mock(Customer.class)));
        when(proposalRequestRepositoryPort.countByCustomerIdAndStatus(customerId, RequestStatus.PENDING_ANALYSIS)).thenReturn(2L);

        assertThrows(BusinessRuleException.class, () ->
                createProposalRequestUseCase.createProposalRequest(customerId, vehicle, downPayment));

        verify(proposalRequestRepositoryPort, never()).save(any());
    }
}