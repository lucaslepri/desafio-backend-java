package com.itau.desafio.vendas.architecture;

import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class HexagonalTest extends ArchitectureTestBase {

    @Test
    @DisplayName("Domínio deve ser puro e livre de frameworks")
    /*
     * Nota: O domínio tem dependência do X-Ray, mas é uma exceção
     * momentânea, que eu resolveria com AOP.
     * 
     */
    void domain_should_be_framework_agnostic() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.hibernate..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Domínio não deve depender de outras camadas")
    void domain_should_not_depend_on_other_layers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..",
                        "..infrastructure..");
        rule.check(productionClasses);
    }

    @Test
    @DisplayName("Applicação não deve depender de infraestrutura")
    void application_should_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Adapters de entrada (Web) só devem chamar a camada de Aplicação")
    void web_adapters_should_only_call_application_layer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..infrastructure.adapters.in.web..")
                .should().accessClassesThat().resideInAnyPackage(
                        "..domain.port.out..",
                        "..infrastructure.adapters.out..");
        rule.check(productionClasses);
    }

    @Test
    @DisplayName("Use Cases devem ter sufixo 'UseCaseImpl'")
    void use_cases_should_have_suffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.usecases")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("UseCaseImpl");
        rule.check(productionClasses);
    }

    @Test
    @DisplayName("Adapters de persistência devem implementar uma porta e ter sufixo 'Adapter'")
    void persistence_adapters_should_implement_port_and_have_suffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapters.out.persistence")
                .should().implement(com.itau.desafio.vendas.domain.port.out.CustomerRepositoryPort.class)
                .orShould().implement(com.itau.desafio.vendas.domain.port.out.ProposalRequestRepositoryPort.class)
                .andShould().haveSimpleNameEndingWith("Adapter");
        rule.check(productionClasses);
    }

}
