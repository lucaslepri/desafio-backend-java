package com.itau.desafio.vendas.application.port.in;

import com.itau.desafio.vendas.domain.model.Customer;
import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateCustomerUseCase {
    /**
     * Atualiza as informações de um cliente existente.
     *
     * @param id            O ID unico do cliente a ser atualizado.
     * @param name          O novo nome (se fornecido).
     * @param phoneNumber         O novo telefone (sefornecido).
     * @param monthlyIncome A nova rendamensal (se fornecida)
     * @return O objeto Customer atualizado.
     * @throws CustomerNotFoundException se nenhum cliente com o ID fornecido
     *                                   existir.
     */
    Customer updateCustomer(UUID id, String name, String phoneNumber, BigDecimal monthlyIncome);
}