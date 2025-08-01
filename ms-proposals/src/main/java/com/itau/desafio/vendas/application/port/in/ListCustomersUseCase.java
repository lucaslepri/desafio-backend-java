package com.itau.desafio.vendas.application.port.in;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.itau.desafio.vendas.domain.model.Customer;
import com.itau.desafio.vendas.domain.model.PageQuery;
import com.itau.desafio.vendas.domain.model.PaginatedResult;

@XRayEnabled
public interface ListCustomersUseCase {

    /**
     * Lista todos os clientes paginados.
     *
     * @param pageable Objeto que contém informações de paginação.
     * @return Uma página de clientes
     */
    PaginatedResult<Customer> listAll(PageQuery query);
}