package com.itau.desafio.vendas.domain.exceptions;

public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}