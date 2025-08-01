package com.itau.desafio.vendas.infrastructure.adapters.in.web;

import com.itau.desafio.vendas.application.port.in.CreateProposalRequestUseCase;
import com.itau.desafio.vendas.domain.model.ProposalRequest;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.CreateProposalRequestRequest;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.mapper.VehicleMapper;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/proposal-requests")
public class ProposalRequestController {

    private final CreateProposalRequestUseCase createProposalRequestUseCase;

    public ProposalRequestController(CreateProposalRequestUseCase createProposalRequestUseCase) {
        this.createProposalRequestUseCase = createProposalRequestUseCase;
    }

    @PostMapping
    public ResponseEntity<ProposalRequest> createProposalRequest(@RequestBody @Valid CreateProposalRequestRequest request) {
        ProposalRequest solicitacaoSalva = createProposalRequestUseCase.createProposalRequest(
                request.customerId(),
                VehicleMapper.toDomain(request.vehicle()),
                request.downPayment());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitacaoSalva.getId())
                .toUri();

        return ResponseEntity.created(location).body(solicitacaoSalva);
    }
}
