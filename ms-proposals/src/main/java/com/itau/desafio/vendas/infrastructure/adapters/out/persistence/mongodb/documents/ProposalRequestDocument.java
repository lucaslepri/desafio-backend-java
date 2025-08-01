package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itau.desafio.vendas.domain.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "solicitacoes")
public class ProposalRequestDocument {
    @Id
    private UUID id;

    private UUID customerId;

    private VehicleDocument vehicle;

    private BigDecimal downPayment;

    private RequestStatus status;

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

    @Version
    private Long version;

   
}