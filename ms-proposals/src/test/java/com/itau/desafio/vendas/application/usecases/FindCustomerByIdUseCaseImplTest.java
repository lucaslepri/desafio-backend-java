package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCustomerByIdUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private FindCustomerByIdUseCaseImpl findCustomerByIdUseCase;

    @Test
    @DisplayName("Deve retornar cliente quando o ID existe")
    void deveRetornarCliente_quandoIdExiste() {
        UUID customerId = UUID.randomUUID();

        Customer expectedCustomer = Customer.reconstitute(customerId, "Test Customer", "12345678900", "11999998888",
                new BigDecimal("5000"), CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "system", LocalDateTime.now(), "system");

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(expectedCustomer));

        Optional<Customer> actualCustomerOpt = findCustomerByIdUseCase.findById(customerId);

        assertTrue(actualCustomerOpt.isPresent());
        assertEquals(expectedCustomer, actualCustomerOpt.get());
        verify(customerRepositoryPort, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o ID não existe")
    void deveRetornarOptionalVazio_quandoIdNaoExiste() {
        UUID customerId = UUID.randomUUID();
        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.empty());

        Optional<Customer> actualCustomerOpt = findCustomerByIdUseCase.findById(customerId);

        assertTrue(actualCustomerOpt.isEmpty());
        verify(customerRepositoryPort, times(1)).findById(customerId);
    }
}