package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateCustomerRequest(
        @Size(min = 2, max = 100) String fullName,

        String phoneNumber,

        @Positive(message = "A renda mensal deve ser um valor positivo.") BigDecimal monthlyIncome) {
}