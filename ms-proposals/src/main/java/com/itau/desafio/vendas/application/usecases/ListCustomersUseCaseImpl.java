package com.itau.desafio.vendas.application.usecases;

import com.itau.desafio.vendas.application.port.in.ListCustomersUseCase;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;

public class ListCustomersUseCaseImpl implements ListCustomersUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public ListCustomersUseCaseImpl(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    public PaginatedResult<Customer> listAll(PageQuery query) {
        return customerRepositoryPort.findAll(query);
    }

}
