package com.itau.desafio.vendas.architecture.domain.customer;

import com.itau.desafio.vendas.architecture.ArchitectureTestBase;
import com.itau.desafio.vendas.domain.model.Customer;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;

class CustomerArchitectureTest extends ArchitectureTestBase {

    @Test
    @DisplayName("Domínio não deve depender de infraestrutura")
    void domain_should_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Domínio não deve depender de aplicação")
    void infrastructure_should_not_depend_on_domain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..application..");

        rule.check(importedClasses);
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
    @DisplayName("Customer não deve depender de classes de infraestrutura")
    void customer_should_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(importedClasses);
    }

    @Test
    void customerCreate_should_only_be_called_by_usecases_or_tests() {

        DescribedPredicate<JavaClass> areUseCases = resideInAPackage("com.itau.desafio.vendas.application.usecases..");

        DescribedPredicate<JavaClass> isTheEntityItself = assignableTo(Customer.class);

        DescribedPredicate<JavaClass> areTestClasses = simpleNameEndingWith("Test")
                .or(simpleNameEndingWith("IT"));

        DescribedPredicate<JavaClass> areAllowedToCallCreate = areUseCases
                .or(isTheEntityItself)
                .or(areTestClasses);

        ArchRule rule = methods()
                .that().areDeclaredIn(Customer.class)
                .and().haveName("create")
                .should().onlyBeCalled().byClassesThat(areAllowedToCallCreate);

        rule.check(importedClasses);
    }
}