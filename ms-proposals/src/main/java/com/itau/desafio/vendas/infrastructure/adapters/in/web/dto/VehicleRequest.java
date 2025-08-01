package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleRequest(
    @NotBlank(message = "O modelo do veículo é obrigatório")
    String model,

    @NotNull(message = "O ano do veículo é obrigatório")
    Integer manufactureYear,

    @NotNull(message = "O valor do veículo é obrigatório")
    @Positive(message = "O valor do veículo deve ser positivo")
    BigDecimal cost
) {}
