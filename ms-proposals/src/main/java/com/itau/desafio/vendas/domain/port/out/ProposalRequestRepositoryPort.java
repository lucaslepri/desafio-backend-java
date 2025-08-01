package com.itau.desafio.vendas.domain.port.out;

import java.util.UUID;

import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.domain.model.RequestStatus;

public interface ProposalRequestRepositoryPort {
    ProposalRequest save(ProposalRequest solicitacaoproposal);

    long countByCustomerIdAndStatus(UUID customerId, RequestStatus status);
}
