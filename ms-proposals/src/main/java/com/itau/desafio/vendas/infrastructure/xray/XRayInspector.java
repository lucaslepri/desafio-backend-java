package com.itau.desafio.vendas.infrastructure.xray;

import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Componente inspetor AWS X-Ray que fornece capacidades de rastreamento
 * distribuído para a aplicação.
 * Este componente orientado a aspectos intercepta automaticamente chamadas de
 * métodos dentro do pacote vendas
 * e cria subsegmentos X-Ray para monitoramento e observabilidade.
 * 
 * <p>
 * O inspetor estende {@link BaseAbstractXRayInterceptor} para aproveitar a
 * integração AOP do Spring com AWS X-Ray
 * e aplica rastreamento a todas as classes do pacote vendas, excluindo
 * componentes de configuração e infraestrutura
 * para evitar dependências circulares e sobrecarga desnecessária.
 * </p>
 * 
 * <p>
 * Pacotes excluídos do rastreamento:
 * </p>
 * <ul>
 * <li>com.itau.desafio.vendas.infrastructure.config.* - Classes de
 * configuração</li>
 * <li>com.itau.desafio.vendas.infrastructure.xray.* - Classes de infraestrutura
 * do X-Ray</li>
 * <li>VendasApplication - Classe principal da aplicação</li>
 * </ul>
 * 
 * @see BaseAbstractXRayInterceptor
 * @see com.amazonaws.xray.entities.Subsegment
 */
@Aspect
@Component
public class XRayInspector extends BaseAbstractXRayInterceptor {

    @Override
    @Pointcut("within(com.itau.desafio.vendas..*) && " +
            "!within(com.itau.desafio.vendas.infrastructure.config..*) && " +
            "!within(com.itau.desafio.vendas.infrastructure.xray..*) && " +
            "!target(com.itau.desafio.vendas.VendasApplication)")
    public void xrayEnabledClasses() {
    }

    @Override
    protected Map<String, Map<String, Object>> generateMetadata(ProceedingJoinPoint pjp, Subsegment subsegment) {
        return super.generateMetadata(pjp, subsegment);
    }
}