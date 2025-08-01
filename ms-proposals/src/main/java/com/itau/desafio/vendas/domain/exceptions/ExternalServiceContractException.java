package com.itau.desafio.vendas.domain.exceptions;

public class ExternalServiceContractException extends RuntimeException {
    public ExternalServiceContractException(String message) {
        super(message);
    }

    public ExternalServiceContractException(String message, Throwable cause) {
        super(message, cause);
    }
}