package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.domain.exceptions.BusinessRuleException;
import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseImplTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @Mock
    private CpfValidationStrategy cpfValidationStrategy;

    @InjectMocks
    private CreateCustomerUseCaseImpl createCustomerUseCase;

    private String fullName;
    private String cpfComMascaraValido;
    private String cpfLimpoValido;
    private String phoneNumber;
    private BigDecimal monthlyIncome;

    @BeforeEach
    void setUp() {
        fullName = "Fulano de Tal";
        cpfComMascaraValido = "439.910.168-77";
        cpfLimpoValido = "43991016877";
        phoneNumber = "11987654321";
        monthlyIncome = new BigDecimal("5000.00");
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso quando a estratégia de validação retorna um status válido")
    void deveCriarCliente_quandoEstrategiaRetornaStatusValido() {
        when(cpfValidationStrategy.validateCpf(cpfLimpoValido)).thenReturn(CpfStatus.CPF_ATIVO);
        when(customerRepositoryPort.existsByCpf(cpfLimpoValido)).thenReturn(false);

        createCustomerUseCase.createCustomer(fullName, cpfComMascaraValido, phoneNumber, monthlyIncome);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepositoryPort).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertNotNull(savedCustomer);
        assertEquals(CpfStatus.CPF_ATIVO, savedCustomer.getCpfStatus());
        assertEquals(cpfLimpoValido, savedCustomer.getCpf());
        verify(cpfValidationStrategy, times(1)).validateCpf(cpfLimpoValido);
    }

    @Test
    @DisplayName("Deve criar cliente com status PENDENTE quando a estratégia de validação retorna pendente")
    void deveCriarCliente_quandoEstrategiaRetornaPendente() {
        when(cpfValidationStrategy.validateCpf(cpfLimpoValido)).thenReturn(CpfStatus.PENDENTE_VALIDACAO_RECEITA);
        when(customerRepositoryPort.existsByCpf(cpfLimpoValido)).thenReturn(false);

        createCustomerUseCase.createCustomer(fullName, cpfComMascaraValido, phoneNumber, monthlyIncome);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepositoryPort).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertNotNull(savedCustomer);
        assertEquals(CpfStatus.PENDENTE_VALIDACAO_RECEITA, savedCustomer.getCpfStatus());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando CPF já existe no banco de dados")
    void deveLancarExcecao_quandoCpfJaExiste() {
        when(customerRepositoryPort.existsByCpf(cpfLimpoValido)).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            createCustomerUseCase.createCustomer(fullName, cpfComMascaraValido, phoneNumber, monthlyIncome);
        });

        assertEquals("Já existe um cliente cadastrado com o CPF fornecido.", exception.getMessage());
        verify(cpfValidationStrategy, never()).validateCpf(anyString());
        verify(customerRepositoryPort, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao tentar criar cliente com nome completo nulo")
    void deveLancarExcecao_quandoNomeCompletoNulo() {
        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            createCustomerUseCase.createCustomer(null, cpfComMascaraValido, phoneNumber, monthlyIncome);
        });

        assertEquals("Nome completo é obrigatório.", exception.getMessage());
        verify(customerRepositoryPort, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao tentar criar cliente com renda mensal inválida")
    void deveLancarExcecao_quandoRendaMensalInvalida() {
        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            createCustomerUseCase.createCustomer(fullName, cpfComMascaraValido, phoneNumber, BigDecimal.ZERO);
        });

        assertEquals("Renda mensal deve ser um valor positivo.", exception.getMessage());
        verify(customerRepositoryPort, never()).save(any(Customer.class));
    }
}