package com.itau.desafio.vendas.application.port.in;

import com.itau.desafio.vendas.domain.model.Customer;
import java.util.Optional;
import java.util.UUID;

public interface FindCustomerByIdUseCase {

    Optional<Customer> findById(UUID id);
}