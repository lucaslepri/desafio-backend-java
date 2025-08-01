package com.itau.desafio.vendas.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(UUID id);

    PaginatedResult<Customer> findAll(PageQuery query);

    boolean existsByCpf(String cpf);

    boolean existsById(UUID id);

    Customer save(Customer customer);

    void deleteById(UUID id);
}
