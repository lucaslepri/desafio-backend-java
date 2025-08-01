package com.itau.desafio.vendas.infrastructure.config.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'AGENTE_NEGOCIOS')")
public @interface IsAdminOrBusinessAgent {
}