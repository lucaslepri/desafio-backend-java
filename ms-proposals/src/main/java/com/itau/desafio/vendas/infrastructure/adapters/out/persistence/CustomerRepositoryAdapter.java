package com.itau.desafio.vendas.infrastructure.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.MongoCustomerRepository;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.mappers.CustomerPersistenceMapper;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final MongoCustomerRepository mongoCustomerRepository;
    private final CustomerPersistenceMapper customerMapper;

    public CustomerRepositoryAdapter(MongoCustomerRepository mongoCustomerRepository,
            CustomerPersistenceMapper customerMapper) {
        this.mongoCustomerRepository = mongoCustomerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Cacheable(value = "customers", key = "#id")
    public Optional<Customer> findById(UUID id) {
        return mongoCustomerRepository.findById(id)
                .map(customerMapper::toDomainModel);
    }

    @Override
    public PaginatedResult<Customer> findAll(PageQuery query) {
        Pageable pageable = PageRequest.of(query.pageNumber(), query.pageSize());

        Page<CustomerDocument> documentPage = mongoCustomerRepository.findAll(pageable);

        Page<Customer> customerPage = documentPage.map(customerMapper::toDomainModel);

        return new PaginatedResult<>(
                customerPage.getContent(),
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages());
    }

    @Override
    @CachePut(value = "customers", key = "#result.id")
    public Customer save(Customer customer) {
        CustomerDocument document = customerMapper.fromDomainModel(customer);
        CustomerDocument savedDocument = mongoCustomerRepository.save(document);
        return customerMapper.toDomainModel(savedDocument);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return mongoCustomerRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsById(UUID id) {
        return mongoCustomerRepository.existsById(id);
    }

    @Override
    @CacheEvict(value = "customers", key = "#id")
    public void deleteById(UUID id) {
        mongoCustomerRepository.deleteById(id);
    }
}
