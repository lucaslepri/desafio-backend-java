package com.itau.desafio.vendas.domain.model;

public record PageQuery(int pageNumber, int pageSize) {
    public PageQuery {
        if (pageNumber < 0)
            throw new IllegalArgumentException("Número da página não pode ser negativo.");
        if (pageSize <= 0)
            throw new IllegalArgumentException("Tamanho da página deve ser positivo.");
    }
}