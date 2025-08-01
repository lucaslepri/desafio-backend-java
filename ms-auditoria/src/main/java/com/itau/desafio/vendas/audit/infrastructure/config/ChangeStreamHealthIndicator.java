package com.itau.desafio.vendas.audit.infrastructure.config;

import com.itau.desafio.vendas.audit.infrastructure.adapter.in.changestream.MongoChangeStreamListenerAdapter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ChangeStreamHealthIndicator implements HealthIndicator {

    private final MongoChangeStreamListenerAdapter listenerAdapter;

    public ChangeStreamHealthIndicator(MongoChangeStreamListenerAdapter listenerAdapter) {
        this.listenerAdapter = listenerAdapter;
    }

    @Override
    public Health health() {
        if (listenerAdapter.isRunning()) {
            return Health.up().withDetail("status", "Listener está executando ativamente.").build();
        } else {
            Exception lastError = listenerAdapter.getLastException();
            if (lastError != null) {
                return Health.down()
                        .withDetail("status", "Listener parou devido a um erro.")
                        .withException(lastError)
                        .build();
            } else {
                return Health.down()
                        .withDetail("status",
                                "Listener não está executando (pode estar inicializando ou foi encerrado normalmente).")
                        .build();
            }
        }
    }
}