package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCustomerUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private DeleteCustomerUseCaseImpl deleteCustomerUseCase;

    @Test
    @DisplayName("Deve deletar cliente com sucesso quando o ID existe")
    void deveDeletarCliente_quandoIdExiste() {
        UUID customerId = UUID.randomUUID();
        when(customerRepositoryPort.existsById(customerId)).thenReturn(true);

        assertDoesNotThrow(() -> deleteCustomerUseCase.deleteCustomer(customerId));

        verify(customerRepositoryPort, times(1)).existsById(customerId);
        verify(customerRepositoryPort, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("Deve lançar CustomerNotFoundException ao tentar deletar cliente inexistente")
    void deveLancarExcecao_aoDeletarClienteInexistente() {
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> {
            deleteCustomerUseCase.deleteCustomer(nonExistentId);
        });

        verify(customerRepositoryPort, times(1)).existsById(nonExistentId);
        verify(customerRepositoryPort, never()).deleteById(any(UUID.class));
    }
}