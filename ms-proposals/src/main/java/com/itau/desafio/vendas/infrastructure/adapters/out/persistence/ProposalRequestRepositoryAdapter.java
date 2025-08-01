package com.itau.desafio.vendas.infrastructure.adapters.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.domain.model.RequestStatus;
import com.itau.desafio.vendas.domain.port.out.ProposalRequestRepositoryPort;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.MongoProposalRequestRepository;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.ProposalRequestDocument;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.mappers.ProposalRequestPersistenceMapper;

@Component
public class ProposalRequestRepositoryAdapter implements ProposalRequestRepositoryPort {

    private final MongoProposalRequestRepository mongoProposalRequestRepository;
    private final ProposalRequestPersistenceMapper proposalRequestMapper;

    public ProposalRequestRepositoryAdapter(MongoProposalRequestRepository mongoProposalRequestRepository, 
                                        ProposalRequestPersistenceMapper proposalRequestMapper) {
        this.mongoProposalRequestRepository = mongoProposalRequestRepository;
        this.proposalRequestMapper = proposalRequestMapper;
    }

    @Override
    public ProposalRequest save(ProposalRequest solicitacaoproposal) {
        ProposalRequestDocument document = proposalRequestMapper.fromDomainModel(solicitacaoproposal);
        ProposalRequestDocument savedDocument = mongoProposalRequestRepository.save(document);
        return proposalRequestMapper.toDomainModel(savedDocument);
    }

    @Override
    public long countByCustomerIdAndStatus(UUID customerId, RequestStatus status) {
        return mongoProposalRequestRepository.countByCustomerIdAndStatus(customerId, status);
    }
}