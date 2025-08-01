package com.itau.desafio.vendas.application.port.in;

import java.util.UUID;

public interface DeleteCustomerUseCase {
    /**
     * Exclui um cliente pelo seu ID único.
     *
     * @param id O ID do cliente a ser excluído.
     * @throws CustomerNotFoundException se nenhum cliente com o ID fornecido
     *                                   existir.
     */
    void deleteCustomer(UUID id);
}