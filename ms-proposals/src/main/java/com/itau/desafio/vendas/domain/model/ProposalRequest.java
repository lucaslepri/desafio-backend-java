package com.itau.desafio.vendas.domain.model;

import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ProposalRequest {

    private final UUID id;
    private final UUID customerId;
    private Vehicle vehicle;
    private BigDecimal downPayment;
    private RequestStatus status;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    private ProposalRequest(UUID customerId, Vehicle vehicle, BigDecimal downPayment, String createdBy) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.vehicle = vehicle;
        this.downPayment = downPayment;
        this.status = RequestStatus.PENDING_ANALYSIS;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.lastModifiedAt = this.createdAt;
        this.lastModifiedBy = createdBy;
    }

    private ProposalRequest(UUID id, UUID customerId, Vehicle vehicle, BigDecimal downPayment, RequestStatus status,
            LocalDateTime createdAt, String createdBy, LocalDateTime lastModifiedAt, String lastModifiedBy) {
        this.id = id;
        this.customerId = customerId;
        this.vehicle = vehicle;
        this.downPayment = downPayment;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;
    }

    public static ProposalRequest create(UUID customerId, Vehicle vehicle, BigDecimal downPayment, String createdBy) {
        validate(customerId, vehicle, downPayment);
        return new ProposalRequest(customerId, vehicle, downPayment, createdBy);
    }

    public static ProposalRequest reconstitute(UUID id, UUID customerId, Vehicle vehicle, BigDecimal downPayment,
            RequestStatus status, LocalDateTime createdAt, String createdBy, LocalDateTime lastModifiedAt,
            String lastModifiedBy) {
        return new ProposalRequest(id, customerId, vehicle, downPayment, status, createdAt, createdBy, lastModifiedAt,
                lastModifiedBy);
    }

    private static void validate(UUID customerId, Vehicle vehicle, BigDecimal downPayment) {
        if (customerId == null) {
            throw new DomainValidationException("ID do cliente é obrigatório para a proposta.");
        }
        if (vehicle == null) {
            throw new DomainValidationException("Veículo é obrigatório para a proposta.");
        }
        if (downPayment == null || downPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainValidationException("Valor de entrada não pode ser negativo.");
        }
        if (downPayment.compareTo(vehicle.getCost()) >= 0) {
            throw new DomainValidationException("Valor de entrada não pode ser maior ou igual ao custo do veículo.");
        }
    }

    public void approve(String approvedBy) {
        this.status = RequestStatus.APPROVED;
        this.lastModifiedAt = LocalDateTime.now();
        this.lastModifiedBy = approvedBy;
    }

    public void reject(String rejectedBy) {
        this.status = RequestStatus.REJECTED;
        this.lastModifiedAt = LocalDateTime.now();
        this.lastModifiedBy = rejectedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProposalRequest that = (ProposalRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}