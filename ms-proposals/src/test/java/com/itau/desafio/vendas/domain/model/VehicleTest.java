package com.itau.desafio.vendas.domain.model;

import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    @DisplayName("Deve criar Vehicle com sucesso quando os dados são válidos")
    void create_shouldSucceed_whenDataIsValid() {
        Vehicle vehicle = Vehicle.create("Honda Civic", new BigDecimal("80000"), 2022, "system");

        assertNotNull(vehicle);
        assertEquals("Honda Civic", vehicle.getModel());
        assertEquals(new BigDecimal("80000"), vehicle.getCost());
        assertEquals(2022, vehicle.getManufactureYear());
        assertNotNull(vehicle.getCreatedAt());
        assertEquals("system", vehicle.getCreatedBy());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  " })
    @DisplayName("Deve lançar DomainValidationException ao criar com modelo inválido")
    void create_shouldThrowException_whenModelIsInvalid(String invalidModel) {
        DomainValidationException exception = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create(invalidModel, new BigDecimal("80000"), 2022, "system");
        });
        assertEquals("Modelo do veículo é obrigatório.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao criar com custo nulo, zero ou negativo")
    void create_shouldThrowException_whenCostIsInvalid() {
        DomainValidationException exNull = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create("Honda Civic", null, 2022, "system");
        });
        assertEquals("Custo do veículo deve ser positivo.", exNull.getMessage());

        DomainValidationException exZero = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create("Honda Civic", BigDecimal.ZERO, 2022, "system");
        });
        assertEquals("Custo do veículo deve ser positivo.", exZero.getMessage());

        DomainValidationException exNegative = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create("Honda Civic", new BigDecimal("-1"), 2022, "system");
        });
        assertEquals("Custo do veículo deve ser positivo.", exNegative.getMessage());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException ao criar com ano de fabricação inválido")
    void create_shouldThrowException_whenManufactureYearIsInvalid() {
        DomainValidationException exNull = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create("Honda Civic", new BigDecimal("80000"), null, "system");
        });
        assertEquals("Ano de fabricação inválido.", exNull.getMessage());

        int veryFutureYear = LocalDateTime.now().getYear() + 2;
        DomainValidationException exFuture = assertThrows(DomainValidationException.class, () -> {
            Vehicle.create("Honda Civic", new BigDecimal("80000"), veryFutureYear, "system");
        });
        assertEquals("Ano de fabricação inválido.", exFuture.getMessage());
    }

    @Test
    @DisplayName("Deve permitir criar veículo com ano de fabricação do próximo ano")
    void create_shouldAllowNextManufactureYear() {
        int nextYear = LocalDateTime.now().getYear() + 1;

        assertDoesNotThrow(() -> {
            Vehicle.create("Honda Civic", new BigDecimal("80000"), nextYear, "system");
        });
    }
}