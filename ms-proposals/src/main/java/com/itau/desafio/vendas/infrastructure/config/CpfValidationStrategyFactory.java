package com.itau.desafio.vendas.infrastructure.config;

import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Classe de configuração de fábrica responsável por criar e configurar os beans
 * de estratégia de validação de CPF.
 * Esta fábrica permite a seleção dinâmica de provedores de validação de CPF
 * através de propriedades da aplicação.
 * 
 * <p>
 * A fábrica suporta duas estratégias de validação:
 * <ul>
 * <li><strong>receitaFederal</strong> - Usa o serviço da Receita Federal para
 * validação de CPF (padrão)</li>
 * <li><strong>jatoCpf</strong> - Usa o serviço JatoCpf para validação de
 * CPF</li>
 * </ul>
 * 
 * <p>
 * A seleção da estratégia é controlada pela propriedade
 * {@code cpf.validator.provider},
 * que por padrão é "receitaFederal" se não especificada.
 * 
 * <p>
 * Esta configuração utiliza {@code @RefreshScope} para permitir a
 * reconfiguração em tempo de execução
 * da estratégia de validação sem reiniciar a aplicação.
 */
@Configuration
@Slf4j
public class CpfValidationStrategyFactory {

    @Bean
    @Primary
    @RefreshScope
    public CpfValidationStrategy cpfValidationStrategy(
            FeatureToggleService featureToggleService, // 1. Injete o serviço de toggle
            @Qualifier("receitaFederal") CpfValidationStrategy receitaFederalStrategy,
            @Qualifier("jatoCpf") CpfValidationStrategy jatoCpfStrategy) {

        String provider = featureToggleService.getCpfValidatorProvider();
        log.info("Configurando provedor de validação de CPF. Provedor selecionado: '{}'", provider);

        if ("jatoCpf".equalsIgnoreCase(provider)) {
            return jatoCpfStrategy;
        }
        return receitaFederalStrategy;
    }
}