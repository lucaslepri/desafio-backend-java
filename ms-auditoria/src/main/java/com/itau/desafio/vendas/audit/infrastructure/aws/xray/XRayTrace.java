package com.itau.desafio.vendas.audit.infrastructure.aws.xray;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar métodos que devem ter um subsegmento
 * do AWS X-Ray criado automaticamente.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRayTrace {
}