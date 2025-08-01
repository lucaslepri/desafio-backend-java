package com.itau.desafio.vendas.audit.infrastructure.config;

import com.itau.desafio.vendas.audit.application.port.in.ProcessAuditEventUseCase;
import com.itau.desafio.vendas.audit.application.usecase.ProcessAuditEventUseCaseImpl;
import com.itau.desafio.vendas.audit.domain.port.out.AuditEventExporterPort;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class BeanConfiguration {

    @Bean
    public ProcessAuditEventUseCase processAuditEventUseCase(
            AuditEventExporterPort auditEventExporter,
            MeterRegistry meterRegistry) {
        return new ProcessAuditEventUseCaseImpl(auditEventExporter, meterRegistry);
    }

    @Bean(name = "auditTaskExecutor")
    public Executor auditTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setThreadNamePrefix("AuditEvent-vt-");
        return executor;
    }
}