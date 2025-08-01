/**
 * Serviço responsável por gerenciar feature toggles e propriedades de configuração
 * que podem ser dinamicamente atualizadas sem reiniciar a aplicação.
 * 
 * Este serviço fornece acesso a flags de funcionalidades configuráveis e configurações
 * utilizadas em toda a aplicação, particularmente para a seleção do provedor de validação de CPF.
*/

/**
 * Constrói um novo FeatureToggleService com o provedor de validação de CPF especificado.
 * 
 * @param cpfValidatorProvider o nome do provedor de validação de CPF a ser utilizado.
 *                             Padrão é "receitaFederal" se não especificado na configuração.
 */

/**
 * Recupera o nome do provedor de validação de CPF atualmente configurado.
 * 
 * Este método retorna o provedor que deve ser utilizado para operações de validação de CPF
 * em toda a aplicação.
 * 
 * @return o nome do provedor de validação de CPF (por exemplo, "receitaFederal")
 */
package com.itau.desafio.vendas.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
public class FeatureToggleService {

    private final String cpfValidatorProvider;

    public FeatureToggleService(
            @Value("${cpf.validator.provider:receitaFederal}") String cpfValidatorProvider) {
        this.cpfValidatorProvider = cpfValidatorProvider;
    }

    public String getCpfValidatorProvider() {
        return this.cpfValidatorProvider;
    }
}