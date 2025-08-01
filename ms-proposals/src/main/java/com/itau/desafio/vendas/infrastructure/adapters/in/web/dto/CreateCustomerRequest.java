package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CPF;


public record CreateCustomerRequest(
        @NotBlank(message = "O nome não pode estar em branco")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        String fullName,

        @CPF(message = "O CPF fornecido é inválido")
        String cpf,

        @NotBlank(message = "O telefone não pode estar em branco")
        String phoneNumber,

        @NotNull(message = "A renda mensal é obrigatória")
        @Positive(message = "A renda mensal deve ser um valor positivo.")
        BigDecimal monthlyIncome
) {
}