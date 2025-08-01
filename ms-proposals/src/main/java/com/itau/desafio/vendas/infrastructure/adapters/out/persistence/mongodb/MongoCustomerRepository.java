package com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb;

import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MongoCustomerRepository extends MongoRepository<CustomerDocument, UUID> {

    boolean existsByCpf(String cpf);

}
