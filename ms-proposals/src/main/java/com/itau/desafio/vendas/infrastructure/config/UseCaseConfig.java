package com.itau.desafio.vendas.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.itau.desafio.vendas.application.port.in.CreateCustomerUseCase;
import com.itau.desafio.vendas.application.port.in.CreateProposalRequestUseCase;
import com.itau.desafio.vendas.application.port.in.DeleteCustomerUseCase;
import com.itau.desafio.vendas.application.port.in.FindCustomerByIdUseCase;
import com.itau.desafio.vendas.application.port.in.ListCustomersUseCase;
import com.itau.desafio.vendas.application.port.in.UpdateCustomerUseCase;
import com.itau.desafio.vendas.application.usecases.*;
import com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort;
import com.itau.desafio.vendas.domain.port.out.ProposalRequestRepositoryPort;
import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UseCaseConfig {

    @Bean
    public CreateProposalRequestUseCase createProposalRequestUseCase(
            CustomerRepositoryPort c,
            ProposalRequestRepositoryPort s,
            MeterRegistry m) {
        return new CreateProposalRequestUseCaseImpl(
                c,
                s,
                m);
    }

    @Bean
    public CreateCustomerUseCase createCustomerUseCase(
            CustomerRepositoryPort c,
            CpfValidationStrategy r,
            MeterRegistry m) {
        return new CreateCustomerUseCaseImpl(c, r, m);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(
            CustomerRepositoryPort c) {
        return new ListCustomersUseCaseImpl(c);
    }

    @Bean
    public FindCustomerByIdUseCase findCustomerByIdUseCase(
            CustomerRepositoryPort c) {
        return new FindCustomerByIdUseCaseImpl(c);
    }

    @Bean
    public UpdateCustomerUseCase updateCustomerUseCase(
            CustomerRepositoryPort c) {
        return new UpdateCustomerUseCaseImpl(c);
    }

    @Bean
    public DeleteCustomerUseCase deleteCustomerUseCase(
            CustomerRepositoryPort c) {
        return new DeleteCustomerUseCaseImpl(c);
    }

}