package com.itau.desafio.vendas.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class CyclicDependencyTest extends ArchitectureTestBase {

    @Test
    @DisplayName("Não deve haver dependências cíclicas entre pacotes")
    void no_cyclic_dependencies_between_packages() {
        ArchRule rule = slices()
                .matching("com.itau.desafio.vendas.(*)..")
                .should().beFreeOfCycles();

        rule.check(importedClasses);
    }

}
