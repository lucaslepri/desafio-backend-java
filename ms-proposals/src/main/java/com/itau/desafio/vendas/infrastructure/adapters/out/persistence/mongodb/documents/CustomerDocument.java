package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itau.desafio.vendas.domain.model.CpfStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "customers")
@Builder
public class CustomerDocument implements Persistable<UUID> {
    @Id
    private UUID id;

    private String cpf;    

    private CpfStatus cpfStatus;

    private String fullName;

    private String phoneNumber;

    private BigDecimal monthlyIncome;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @LastModifiedBy
    private String lastModifiedBy;

    @JsonIgnore
    private boolean isNew;

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }

}
