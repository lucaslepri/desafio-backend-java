package com.itau.desafio.vendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication(exclude = {
        CompositeDiscoveryClientAutoConfiguration.class,
        SimpleDiscoveryClientAutoConfiguration.class
})
@EnableCaching
@EnableMongoAuditing
@OpenAPIDefinition(info = @Info(title = "API de Vendas", version = "v1", description = "API para o Desafio Técnico Itaú Unibanco"))
public class VendasApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendasApplication.class, args);
    }

}
