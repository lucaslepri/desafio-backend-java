package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListCustomersUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private ListCustomersUseCaseImpl listCustomersUseCase;

    @Test
    @DisplayName("Deve retornar lista paginada de clientes ao chamar repositório")
    void deveRetornarListaPaginadaDeClientes_aoChamarRepositorio() {
        PageQuery query = new PageQuery(0, 10);

        List<Customer> customersList = List.of(
                Customer.reconstitute(UUID.randomUUID(), "Customer 1", "111", "111", BigDecimal.ONE,
                        CpfStatus.CPF_ATIVO,
                        LocalDateTime.now(), "sys", LocalDateTime.now(), "sys"),
                Customer.reconstitute(UUID.randomUUID(), "Customer 2", "222", "222", BigDecimal.TEN,
                        CpfStatus.PENDENTE_VALIDACAO_RECEITA,
                        LocalDateTime.now(), "sys", LocalDateTime.now(), "sys"));

        PaginatedResult<Customer> expectedResult = new PaginatedResult<>(
                customersList, 0, 10, (long) customersList.size(), 1);

        when(customerRepositoryPort.findAll(query)).thenReturn(expectedResult);

        PaginatedResult<Customer> actualResult = listCustomersUseCase.listAll(query);

        assertNotNull(actualResult);
        assertEquals(2, actualResult.totalElements());
        assertEquals("Customer 1", actualResult.content().get(0).getFullName());

        verify(customerRepositoryPort, times(1)).findAll(query);
    }

}