package com.itau.desafio.vendas.domain.model;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages) {
}