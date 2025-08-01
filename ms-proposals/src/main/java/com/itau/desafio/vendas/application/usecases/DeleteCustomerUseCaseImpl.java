package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.application.port.in.DeleteCustomerUseCase;
import com.itau.desafio.vendas.domain.exceptions.CustomerNotFoundException;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;

import java.util.UUID;

public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public DeleteCustomerUseCaseImpl(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public void deleteCustomer(UUID id) {
        if (!customerRepositoryPort.existsById(id)) {
            throw new CustomerNotFoundException("Cliente com ID " + id + " não encontrado.");
        }
        customerRepositoryPort.deleteById(id);
    }
}