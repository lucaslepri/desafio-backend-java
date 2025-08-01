package com.itau.desafio.vendas.application.usecases;

import java.math.BigDecimal;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import com.itau.desafio.vendas.application.port.in.CreateCustomerUseCase;
import com.itau.desafio.vendas.domain.exceptions.BusinessRuleException;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;

@Slf4j
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final CpfValidationStrategy cpfValidationStrategy;
    private final CPFValidator localCpfValidator = new CPFValidator();
    private final Counter createdCustomersCounter;

    public CreateCustomerUseCaseImpl(CustomerRepositoryPort customerRepositoryPort,
            CpfValidationStrategy cpfValidationStrategy,
            MeterRegistry meterRegistry) {
        this.customerRepositoryPort = customerRepositoryPort;
        this.cpfValidationStrategy = cpfValidationStrategy;
        this.createdCustomersCounter = Counter.builder("customers.created")
                .description("Número de novos clientes")
                .register(meterRegistry);
    }

    @Override
    public Customer createCustomer(String fullName, String cpf, String phoneNumber, BigDecimal monthlyIncome) {
        String cleanedCpf = cpf.replaceAll("[^0-9]", "");

        isCpfFormatValid(cleanedCpf);

        validateCpfUniqueness(cleanedCpf);

        CpfStatus customerCpfStatus = cpfValidationStrategy.validateCpf(cleanedCpf);

        Customer newCustomer = Customer.create(fullName, cleanedCpf, phoneNumber, monthlyIncome, customerCpfStatus);

        Customer createdCustomer = customerRepositoryPort.save(newCustomer);
        createdCustomersCounter.increment();

        log.info("Cliente criado com sucesso: {}", createdCustomer.getId());

        return createdCustomer;
    }

    private void validateCpfUniqueness(String cpf) {
        if (customerRepositoryPort.existsByCpf(cpf)) {
            throw new BusinessRuleException("Já existe um cliente cadastrado com o CPF fornecido.");
        }
    }

    private boolean isCpfFormatValid(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }
        try {
            localCpfValidator.assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            log.warn("Validação do CPF falhou: {}", e.getMessage());
            return false;
        }
    }
}