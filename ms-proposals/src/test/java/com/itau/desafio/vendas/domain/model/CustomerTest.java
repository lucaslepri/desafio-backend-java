package com.itau.desafio.vendas.domain.model;

import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    @DisplayName("Deve criar Customer com sucesso quando os dados são válidos usando o factory method sem auditoria")
    void create_shouldSucceed_whenDataIsValidWithoutAudit() {
        Customer customer = Customer.create("Valid Name", "12345678900", "11999998888", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Valid Name", customer.getFullName());
        assertEquals(CpfStatus.CPF_ATIVO, customer.getCpfStatus());
        assertNull(customer.getCreatedAt());
        assertNull(customer.getCreatedBy());
    }

    @Test
    @DisplayName("Deve criar Customer com sucesso quando os dados são válidos usando o factory method com auditoria")
    void create_shouldSucceed_whenDataIsValidWithAudit() {
        Customer customer = Customer.create("Valid Name", "12345678900", "11999998888", new BigDecimal("1000"),
                CpfStatus.CPF_ATIVO);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Valid Name", customer.getFullName());
        assertEquals(CpfStatus.CPF_ATIVO, customer.getCpfStatus());
    }

    @ParameterizedTest
    @MethodSource("invalidCustomerDataProvider")
    @DisplayName("Deve lançar DomainValidationException ao criar com dados inválidos")
    void create_shouldThrowException_whenDataIsInvalid(String name, String cpf, String phone, BigDecimal income,
            CpfStatus status, String expectedMessage) {
        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            Customer.create(name, cpf, phone, income, status);
        });
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Object[]> invalidCustomerDataProvider() {
        return Stream.of(
                new Object[] { null, "123", "123", BigDecimal.TEN, CpfStatus.CPF_ATIVO,
                        "Nome completo é obrigatório." },
                new Object[] { "  ", "123", "123", BigDecimal.TEN, CpfStatus.CPF_ATIVO,
                        "Nome completo é obrigatório." },
                new Object[] { "Valid Name", null, "123", BigDecimal.TEN, CpfStatus.CPF_ATIVO, "CPF é obrigatório." },
                new Object[] { "Valid Name", " ", "123", BigDecimal.TEN, CpfStatus.CPF_ATIVO, "CPF é obrigatório." },
                new Object[] { "Valid Name", "123", null, BigDecimal.TEN, CpfStatus.CPF_ATIVO,
                        "Número de telefone é obrigatório." },
                new Object[] { "Valid Name", "123", "  ", BigDecimal.TEN, CpfStatus.CPF_ATIVO,
                        "Número de telefone é obrigatório." },
                new Object[] { "Valid Name", "123", "123", null, CpfStatus.CPF_ATIVO,
                        "Renda mensal deve ser um valor positivo." },
                new Object[] { "Valid Name", "123", "123", BigDecimal.ZERO, CpfStatus.CPF_ATIVO,
                        "Renda mensal deve ser um valor positivo." },
                new Object[] { "Valid Name", "123", "123", new BigDecimal("-1"), CpfStatus.CPF_ATIVO,
                        "Renda mensal deve ser um valor positivo." },
                new Object[] { "Valid Name", "123", "123", BigDecimal.TEN, null, "Status do CPF é obrigatório." });
    }

    @Test
    @DisplayName("Deve atualizar informações do cliente com sucesso")
    void updateInfo_shouldUpdateFieldsCorrectly() {
        Customer customer = reconstituteTestCustomer();
        customer.updateInfo("New Name", "222", new BigDecimal("2000"));

        assertEquals("New Name", customer.getFullName());
        assertEquals("222", customer.getPhoneNumber());
        assertEquals(new BigDecimal("2000"), customer.getMonthlyIncome());
    }

    @Test
    @DisplayName("Não deve atualizar campos quando os novos valores são nulos ou vazios")
    void updateInfo_shouldNotUpdate_whenValuesAreNullOrBlank() {
        Customer customer = reconstituteTestCustomer();
        customer.updateInfo(null, "   ", null);

        assertEquals("Old Name", customer.getFullName());
        assertEquals("111", customer.getPhoneNumber());
        assertEquals(BigDecimal.ONE, customer.getMonthlyIncome());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao atualizar com renda negativa")
    void updateInfo_shouldThrowException_whenMonthlyIncomeIsNegative() {
        Customer customer = reconstituteTestCustomer();

        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> {
            customer.updateInfo("New Name", "222", new BigDecimal("-100"));
        });
        assertEquals("Renda mensal deve ser um valor positivo.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar status do CPF com sucesso")
    void updateCpfStatus_shouldSucceed_whenStatusIsValid() {
        Customer customer = reconstituteTestCustomer();
        customer.updateCpfStatus(CpfStatus.CPF_BLOQUEADO);
        assertEquals(CpfStatus.CPF_BLOQUEADO, customer.getCpfStatus());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao atualizar status do CPF para nulo")
    void updateCpfStatus_shouldThrowException_whenStatusIsNull() {
        Customer customer = reconstituteTestCustomer();
        DomainValidationException ex = assertThrows(DomainValidationException.class, () -> {
            customer.updateCpfStatus(null);
        });
        assertEquals("O status do CPF não pode ser nulo.", ex.getMessage());
    }

    @Test
    @DisplayName("Equals e HashCode devem se basear no ID")
    void equalsAndHashCode_shouldBeBasedOnId() {
        UUID id = UUID.randomUUID();
        Customer customer1 = reconstituteTestCustomerWithId(id);
        Customer customer2 = reconstituteTestCustomerWithId(id);
        Customer customer3 = reconstituteTestCustomer();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertNotEquals(customer1, customer3);
        assertNotEquals(customer1.hashCode(), customer3.hashCode());
        assertNotEquals(null, customer1);
    }

    private Customer reconstituteTestCustomer() {
        return Customer.reconstitute(UUID.randomUUID(), "Old Name", "12345678900", "111", BigDecimal.ONE,
                CpfStatus.CPF_ATIVO, LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");
    }

    private Customer reconstituteTestCustomerWithId(UUID id) {
        return Customer.reconstitute(id, "Old Name", "12345678900", "111", BigDecimal.ONE,
                CpfStatus.CPF_ATIVO, LocalDateTime.now(), "sys", LocalDateTime.now(), "sys");
    }
}