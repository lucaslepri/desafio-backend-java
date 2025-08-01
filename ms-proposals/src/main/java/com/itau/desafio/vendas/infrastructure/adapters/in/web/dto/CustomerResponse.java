package com.itau.desafio.vendas.infrastructure.adapters.in.web.dto;

import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.model.Customer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String fullName,
        String cpf,
        CpfStatus cpfStatus,
        String phoneNumber,
        BigDecimal monthlyIncome,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime lastModifiedAt,
        String lastModifiedBy) {

    public static CustomerResponse fromDomain(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getCpf(),
                customer.getCpfStatus(),
                customer.getPhoneNumber(),
                customer.getMonthlyIncome(),
                customer.getCreatedAt(),
                customer.getCreatedBy(),
                customer.getLastModifiedAt(),
                customer.getLastModifiedBy());
    }
}