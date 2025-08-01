package com.itau.desafio.vendas.domain.model;

import com.itau.desafio.vendas.domain.exceptions.DomainValidationException;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Customer {

    private final UUID id;
    private String fullName;
    private final String cpf;
    private CpfStatus cpfStatus;
    private String phoneNumber;
    private BigDecimal monthlyIncome;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastModifiedAt;
    private String lastModifiedBy;

    /**
     * Construtor privado para ser usado pelos factory methods.
     * Garante que um objeto Customer seja sempre criado em um estado válido.
     * Criação de objeto que representa um cliente já existente.
     * Para os casos de reconstituição de cliente, com dados de
     * criação e modificação.
     */
    private Customer(
            UUID id,
            String fullName,
            String cpf,
            String phoneNumber,
            BigDecimal monthlyIncome,
            CpfStatus cpfStatus,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime lastModifiedAt,
            String lastModifiedBy) {

        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.monthlyIncome = monthlyIncome;
        this.cpfStatus = cpfStatus;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedBy = lastModifiedBy;

        validate();
    }

    /**
     * Construtor privado para ser usado pelos factory methods.
     * Garante que um objeto Customer seja sempre criado em um estado válido.
     * Criação de objeto para os casos de novo ciente.
     */
    private Customer(
            UUID id,
            String fullName,
            String cpf,
            String phoneNumber,
            BigDecimal monthlyIncome,
            CpfStatus cpfStatus) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.phoneNumber = phoneNumber;
        this.monthlyIncome = monthlyIncome;
        this.cpfStatus = cpfStatus;

        validate();
    }

    /**
     * Factory Method para criar uma nova instância de Customer.
     * Este é o ponto de entrada para a criação de novos clientes no domínio.
     *
     * @param fullName         Nome completo do cliente.
     * @param cpf              CPF do cliente (apenas dígitos).
     * @param phoneNumber      Telefone de contato.
     * @param monthlyIncome    Renda mensal.
     * @param cpfStatusReceita O status de validação do CPF determinado pelo caso de
     *                         uso.
     * @param createdBy        Identificador do usuário/sistema que está criando o
     *                         cliente.
     * @return Uma nova instância de Customer.
     */
    public static Customer create(String fullName, String cpf, String phoneNumber, BigDecimal monthlyIncome,
            CpfStatus cpfStatus) {

        UUID newId = UUID.randomUUID();

        return new Customer(newId, fullName, cpf, phoneNumber, monthlyIncome, cpfStatus);
    }

    /**
     * Factory Method para reconstituir um objeto Customer a partir da camada de
     * persistência.
     *
     * @return Uma instância de Customer representando um cliente existente.
     */
    public static Customer reconstitute(UUID id, String fullName, String cpf, String phoneNumber,
            BigDecimal monthlyIncome, CpfStatus cpfStatus,
            LocalDateTime createdAt, String createdBy, LocalDateTime lastModifiedAt, String lastModifiedBy) {

        return new Customer(id, fullName, cpf, phoneNumber, monthlyIncome, cpfStatus,
                createdAt, createdBy, lastModifiedAt, lastModifiedBy);
    }

    /**
     * Atualiza as informações mutáveis do cliente.
     *
     * @param fullName       O novo nome (opcional).
     * @param phoneNumber    O novo telefone (opcional).
     * @param monthlyIncome  A nova renda (opcional).
     * @param lastModifiedBy O identificador de quem está realizando a alteração.
     */
    public void updateInfo(String fullName, String phoneNumber, BigDecimal monthlyIncome) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phoneNumber = phoneNumber;
        }
        if (monthlyIncome != null) {
            this.monthlyIncome = monthlyIncome;
        }

        validate();
    }

    /**
     * Atualiza o status do CPF do cliente.
     * Método a ser usado pelo Sistema ou por usuário com autorização específica.
     * 
     * @param newStatus
     */
    public void updateCpfStatus(CpfStatus newStatus) {
        if (newStatus == null) {
            throw new DomainValidationException("O status do CPF não pode ser nulo.");
        }
        this.cpfStatus = newStatus;
    }

    /**
     * Método privado para validar as invariantes (regras de consistência) do objeto
     * Customer.
     * É chamado na criação e na atualização para garantir que o objeto esteja
     * sempre em um estado válido.
     */
    private void validate() {
        if (this.fullName == null || this.fullName.isBlank()) {
            throw new DomainValidationException("Nome completo é obrigatório.");
        }
        if (this.cpf == null || this.cpf.isBlank()) {
            throw new DomainValidationException("CPF é obrigatório.");
        }
        if (this.phoneNumber == null || this.phoneNumber.isBlank()) {
            throw new DomainValidationException("Número de telefone é obrigatório.");
        }
        if (this.monthlyIncome == null || this.monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Renda mensal deve ser um valor positivo.");
        }
        if (this.id == null) {
            throw new DomainValidationException("ID do cliente não pode ser nulo.");
        }
        if (this.cpfStatus == null) {
            throw new DomainValidationException("Status do CPF é obrigatório.");
        }
        if (this.cpfStatus == null) {
            throw new DomainValidationException("Status do CPF na Receita Federal é obrigatório.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}