package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itau.desafio.vendas.domain.model.PaginatedResult;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(

        List<T> content,
        @JsonProperty("page_number") int pageNumber,
        @JsonProperty("page_size") int pageSize,
        @JsonProperty("total_elements") long totalElements,
        @JsonProperty("total_pages") int totalPages,
        @JsonProperty("is_last") boolean isLast) {
    public PageResponse(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }

    public static <I, O> PageResponse<O> from(PaginatedResult<I> domainResult, Function<I, O> contentMapper) {
        List<O> mappedContent = domainResult.content().stream().map(contentMapper).toList();
        boolean isLast = domainResult.totalPages() == 0 || domainResult.pageNumber() >= domainResult.totalPages() - 1;
        return new PageResponse<>(
                mappedContent,
                domainResult.pageNumber(),
                domainResult.pageSize(),
                domainResult.totalElements(),
                domainResult.totalPages(),
                isLast);
    }
}