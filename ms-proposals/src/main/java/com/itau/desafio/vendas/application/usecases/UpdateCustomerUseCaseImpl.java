package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.application.port.in.UpdateCustomerUseCase;
import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public UpdateCustomerUseCaseImpl(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public Customer updateCustomer(UUID id, String fullName, String phoneNumber, BigDecimal monthlyIncome) {
        Customer existingCustomer = customerRepositoryPort.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente com ID " + id + " não encontrado."));

        existingCustomer.updateInfo(fullName, phoneNumber, monthlyIncome);

        return customerRepositoryPort.save(existingCustomer);
    }
}