package com.itau.desafio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
    exclude = {
        CompositeDiscoveryClientAutoConfiguration.class,
        SimpleDiscoveryClientAutoConfiguration.class
    }
)
@EnableScheduling
@EnableAsync
public class VendasAuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendasAuditApplication.class, args);
    }
}
