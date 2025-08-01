package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb;

import com.itau.desafio.vendas.domain.model.RequestStatus;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.ProposalRequestDocument;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoProposalRequestRepository extends MongoRepository<ProposalRequestDocument, UUID> {
    long countByCustomerIdAndStatus(UUID customerId, RequestStatus status);
}
