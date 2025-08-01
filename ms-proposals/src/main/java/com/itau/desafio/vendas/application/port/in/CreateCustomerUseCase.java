package com.itau.desafio.vendas.application.port.in;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.itau.desafio.vendas.domain.model.Customer;
import java.math.BigDecimal;

@XRayEnabled
public interface CreateCustomerUseCase {

    /*
     * Cria um novo cliente com os dados fornecidos.
     * 
     * @param fullName Nome completo do cliente.
     * 
     * @param cpf CPF do cliente.
     * 
     * @param phoneNumber Telefone do cliente.
     * 
     * @param monthlyIncome Renda mensal do cliente.
     * 
     * @return O cliente recém-criado.
     */
    Customer createCustomer(String fullName, String cpf, String phoneNumber, BigDecimal monthlyIncome);

}