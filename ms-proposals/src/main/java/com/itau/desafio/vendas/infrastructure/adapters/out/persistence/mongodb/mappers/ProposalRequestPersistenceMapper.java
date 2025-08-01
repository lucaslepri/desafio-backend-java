package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.mappers;

import org.springframework.stereotype.Component;
import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.ProposalRequestDocument;

@Component
public class ProposalRequestPersistenceMapper {

    private final VehiclePersistenceMapper vehicleMapper = new VehiclePersistenceMapper();

    public ProposalRequest toDomainModel(ProposalRequestDocument document) {
        if (document == null) {
            return null;
        }
        return ProposalRequest.reconstitute(
                document.getId(),
                document.getCustomerId(),
                vehicleMapper.toDomainModel(document.getVehicle()),
                document.getDownPayment(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getCreatedBy(),
                document.getLastModifiedAt(),
                document.getLastModifiedBy());
    }

    public ProposalRequestDocument fromDomainModel(ProposalRequest proposalRequest) {
        if (proposalRequest == null) {
            return null;
        }

        ProposalRequestDocument document = ProposalRequestDocument.builder()
                .id(proposalRequest.getId())
                .customerId(proposalRequest.getCustomerId())
                .vehicle(vehicleMapper.fromDomainModel(proposalRequest.getVehicle()))
                .downPayment(proposalRequest.getDownPayment())
                .status(proposalRequest.getStatus())
                .build();

        if (proposalRequest.getCreatedAt() == null) {
            document.setNew(true);
        }

        return document;
    }
}