package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Size;

public record ApproveProposalRequest(
        @Size(max = 500) String observacoes) {
}
