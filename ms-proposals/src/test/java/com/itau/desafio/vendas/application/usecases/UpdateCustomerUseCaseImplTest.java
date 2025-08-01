package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private UpdateCustomerUseCaseImpl updateCustomerUseCase;

    @Test
    @DisplayName("Deve atualizar cliente com sucesso quando o ID existe")
    void deveAtualizarCliente_quandoIdExiste() {
        UUID customerId = UUID.randomUUID();

        Customer existingCustomer = Customer.reconstitute(
                customerId, "Old Name", "12345678900", "1111", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");

        String newName = "New Name";
        String newPhoneNumber = "2222";

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepositoryPort.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customerId, newName, newPhoneNumber, null);

        assertNotNull(updatedCustomer);
        assertEquals(newName, updatedCustomer.getFullName());
        assertEquals(newPhoneNumber, updatedCustomer.getPhoneNumber());
        assertEquals(new BigDecimal("1000"), updatedCustomer.getMonthlyIncome());

        verify(customerRepositoryPort, times(1)).findById(customerId);
        verify(customerRepositoryPort, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso quando o ID existe e a renda mensal é atualizada")
    void deveAtualizarClienteComRenda_quandoIdExisteERendaMensalAtualizada() {
        UUID customerId = UUID.randomUUID();

        Customer existingCustomer = Customer.reconstitute(
                customerId, "Old Name", "12345678900", "1111", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");

        String newName = "New Name";
        String newPhoneNumber = "2222";
        BigDecimal newIncome = new BigDecimal("2000");

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepositoryPort.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customerId, newName, newPhoneNumber, newIncome);

        assertNotNull(updatedCustomer);
        assertEquals(newName, updatedCustomer.getFullName());
        assertEquals(newPhoneNumber, updatedCustomer.getPhoneNumber());
        assertEquals(newIncome, updatedCustomer.getMonthlyIncome());

        verify(customerRepositoryPort, times(1)).findById(customerId);
        verify(customerRepositoryPort, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerNotFoundException ao tentar atualizar cliente inexistente")
    void deveLancarExcecao_aoAtualizarClienteInexistente() {
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            updateCustomerUseCase.updateCustomer(nonExistentId, "New Name", "New PhoneNumber", null);
        });

        verify(customerRepositoryPort, times(1)).findById(nonExistentId);
        verify(customerRepositoryPort, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o nome do cliente")
    void deveAtualizarApenasNomeCliente() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = Customer.reconstitute(
                customerId, "Old Name", "12345678900", "1111", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");

        String newName = "New Name";

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepositoryPort.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customerId, newName, null, null);

        assertNotNull(updatedCustomer);
        assertEquals(newName, updatedCustomer.getFullName());
        assertEquals("1111", updatedCustomer.getPhoneNumber());
        assertEquals(new BigDecimal("1000"), updatedCustomer.getMonthlyIncome());
        verify(customerRepositoryPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas o telefone do cliente")
    void deveAtualizarApenasTelefoneCliente() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = Customer.reconstitute(
                customerId, "Old Name", "12345678900", "1111", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");

        String newPhoneNumber = "2222";

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepositoryPort.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(customerId, null, newPhoneNumber, null);

        assertNotNull(updatedCustomer);
        assertEquals("Old Name", updatedCustomer.getFullName());
        assertEquals(newPhoneNumber, updatedCustomer.getPhoneNumber());
        assertEquals(new BigDecimal("1000"), updatedCustomer.getMonthlyIncome());
        verify(customerRepositoryPort).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao tentar atualizar com renda mensal inválida")
    void deveLancarExcecao_aoAtualizarComRendaInvalida() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = Customer.reconstitute(
                customerId, "Old Name", "12345678900", "1111", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO,
                LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");

        BigDecimal invalidIncome = new BigDecimal("-100");

        when(customerRepositoryPort.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        assertThrows(DomainValidationException.class, () -> {
            updateCustomerUseCase.updateCustomer(customerId, null, null, invalidIncome);
        });

        verify(customerRepositoryPort, never()).save(any(Customer.class));
    }
}