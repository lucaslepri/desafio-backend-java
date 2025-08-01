package com.itau.desafio.vendas.infrastructure.adapters.in.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.itau.desafio.vendas.application.port.in.CreateCustomerUseCase;
import com.itau.desafio.vendas.application.port.in.DeleteCustomerUseCase;
import com.itau.desafio.vendas.application.port.in.FindCustomerByIdUseCase;
import com.itau.desafio.vendas.application.port.in.ListCustomersUseCase;
import com.itau.desafio.vendas.application.port.in.UpdateCustomerUseCase;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.CreateCustomerRequest;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.CustomerResponse;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.PageResponse;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;
import com.itau.desafio.vendas.infrastructure.config.security.annotations.IsAdminOrBusinessAgent;
import com.itau.desafio.vendas.infrastructure.config.security.annotations.IsAdminOrBusinessAnalyst;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customers")
@XRayEnabled
public class CustomerController {

    private final ListCustomersUseCase listCustomersUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    public CustomerController(
            ListCustomersUseCase listCustomersUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            CreateCustomerUseCase createCustomerUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase) {
        this.listCustomersUseCase = listCustomersUseCase;
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
        this.createCustomerUseCase = createCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes")
    @IsAdminOrBusinessAnalyst
    public ResponseEntity<PageResponse<CustomerResponse>> listCustomers(Pageable pageable) {
        PageQuery query = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());

        PaginatedResult<Customer> domainResult = listCustomersUseCase.listAll(query);

        PageResponse<CustomerResponse> response = PageResponse.from(domainResult, CustomerResponse::fromDomain);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente por ID")
    public ResponseEntity<EntityModel<CustomerResponse>> findCustomerById(@PathVariable UUID id) {
        return findCustomerByIdUseCase.findById(id)
                .map(CustomerResponse::fromDomain)
                .map(customerResponse -> EntityModel.of(customerResponse,
                        linkTo(methodOn(CustomerController.class).findCustomerById(id)).withSelfRel(),
                        linkTo(methodOn(CustomerController.class).listCustomers(null)).withRel("customers")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria um novo cliente")
    public ResponseEntity<EntityModel<CustomerResponse>> createCustomer(
            @RequestBody @Valid CreateCustomerRequest request) {
        Customer savedCustomer = createCustomerUseCase.createCustomer(
                request.fullName(),
                request.cpf(),
                request.phoneNumber(),
                request.monthlyIncome());

        CustomerResponse responseDto = CustomerResponse.fromDomain(savedCustomer);

        EntityModel<CustomerResponse> customerModel = EntityModel.of(responseDto,
                linkTo(methodOn(CustomerController.class).findCustomerById(savedCustomer.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).listCustomers(Pageable.unpaged())).withRel("customers"));

        return ResponseEntity
                .created(customerModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(customerModel);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza as informações de um cliente")
    @IsAdminOrBusinessAnalyst
    public ResponseEntity<EntityModel<CustomerResponse>> updateCustomer(@PathVariable UUID id,
            @RequestBody @Valid UpdateCustomerRequest request) {
        Customer updatedCustomer = updateCustomerUseCase.updateCustomer(
                id, request.fullName(), request.phoneNumber(), request.monthlyIncome());

        CustomerResponse responseDto = CustomerResponse.fromDomain(updatedCustomer);

        EntityModel<CustomerResponse> customerModel = EntityModel.of(responseDto,
                linkTo(methodOn(CustomerController.class).findCustomerById(id)).withSelfRel(),
                linkTo(methodOn(CustomerController.class).listCustomers(null)).withRel("customers"));

        return ResponseEntity.ok(customerModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um cliente")
    @IsAdminOrBusinessAgent
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        deleteCustomerUseCase.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}