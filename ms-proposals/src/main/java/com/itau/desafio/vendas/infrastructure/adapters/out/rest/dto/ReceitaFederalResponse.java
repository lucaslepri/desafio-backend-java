package com.itau.desafio.vendas.infrastructure.adapters.out.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceitaFederalResponse {
    @JsonProperty("status")
    private String status;
}