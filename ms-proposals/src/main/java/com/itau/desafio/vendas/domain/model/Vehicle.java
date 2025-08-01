package com.itau.desafio.vendas.domain.model;

import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Vehicle {

    private String model;
    private BigDecimal cost;
    private Integer manufactureYear;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    private Vehicle(String model, BigDecimal cost, Integer manufactureYear, String createdBy) {
        this.model = model;
        this.cost = cost;
        this.manufactureYear = manufactureYear;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.lastModifiedAt = this.createdAt;
        this.lastModifiedBy = this.createdBy;
    }

    private Vehicle(String model, BigDecimal cost, Integer manufactureYear, LocalDateTime createdAt, String createdBy,
            LocalDateTime lastModifiedAt, String lastModifiedBy) {
        this.model = model;
        this.cost = cost;
        this.manufactureYear = manufactureYear;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
    }

    public static Vehicle create(String model, BigDecimal cost, Integer manufactureYear, String createdBy) {
        validate(model, cost, manufactureYear);
        return new Vehicle(model, cost, manufactureYear, createdBy);
    }

    public static Vehicle reconstitute(String model, BigDecimal cost, Integer manufactureYear, LocalDateTime createdAt,
            String createdBy, LocalDateTime lastModifiedAt, String lastModifiedBy) {
        return new Vehicle(model, cost, manufactureYear, createdAt, createdBy, lastModifiedAt, lastModifiedBy);
    }

    private static void validate(String model, BigDecimal cost, Integer manufactureYear) {
        if (model == null || model.isBlank()) {
            throw new DomainValidationException("Modelo do veículo é obrigatório.");
        }
        if (cost == null || cost.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Custo do veículo deve ser positivo.");
        }
        if (manufactureYear == null || manufactureYear > LocalDateTime.now().getYear() + 1) {
            throw new DomainValidationException("Ano de fabricação inválido.");
        }
    }
}