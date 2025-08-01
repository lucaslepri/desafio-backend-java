package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProposalRequestRequest(
    @NotNull(message = "O ID é obrigatório")
    UUID customerId,
    
    @NotNull(message = "O veículo deve ser informado")
    @Valid
    VehicleRequest vehicle,
    
    @NotNull(message = "O valor da entrada é obrigatório")
    @PositiveOrZero(message = "O valor da entrada deve ser positivo ou zero")
    BigDecimal downPayment
) {}
