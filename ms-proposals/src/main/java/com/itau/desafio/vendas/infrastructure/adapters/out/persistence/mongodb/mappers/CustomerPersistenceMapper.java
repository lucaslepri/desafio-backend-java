package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.mappers;

import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public Customer toDomainModel(CustomerDocument document) {
        if (document == null) {
            return null;
        }
        return Customer.reconstitute(
                document.getId(),
                document.getFullName(),
                document.getCpf(),
                document.getPhoneNumber(),
                document.getMonthlyIncome(),
                document.getCpfStatus(),
                document.getCreatedAt(),
                document.getCreatedBy(),
                document.getLastModifiedAt(),
                document.getLastModifiedBy()
        );
    }

    public CustomerDocument fromDomainModel(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDocument document = CustomerDocument.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .cpf(customer.getCpf())
                .phoneNumber(customer.getPhoneNumber())
                .monthlyIncome(customer.getMonthlyIncome())
                .cpfStatus(customer.getCpfStatus()) 
                .createdAt(customer.getCreatedAt())
                .build();

        if (customer.getCreatedAt() == null) {
            document.setNew(true);
        }

        return document;
    }
}