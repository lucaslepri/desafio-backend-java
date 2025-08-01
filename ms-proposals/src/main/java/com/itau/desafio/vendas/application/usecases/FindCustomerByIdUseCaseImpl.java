package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.application.port.in.FindCustomerByIdUseCase;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;

import java.util.Optional;
import java.util.UUID;

public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public FindCustomerByIdUseCaseImpl(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerRepositoryPort.findById(id);
    }
}