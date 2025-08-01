package com.itau.desafio.vendas.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Classe base para todos os testes de arquitetura.
 * Configura o ArchUnit para importar apenas o código de produção,
 * ignorando os diretórios de teste.
 */
public abstract class ArchitectureTestBase {

    protected final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.itau.desafio.vendas");

    protected static final JavaClasses productionClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.itau.desafio.vendas");
}