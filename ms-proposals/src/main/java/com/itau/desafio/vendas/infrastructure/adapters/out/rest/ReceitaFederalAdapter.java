package com.itau.desafio.vendas.infrastructure.adapters.out.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.itau.desafio.vendas.domain.exceptions.ExternalServiceContractException;
import com.itau.desafio.vendas.domain.exceptions.ExternalServiceUnavailableException;
import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.domain.port.out.CpfValidationStrategy;
import com.itau.desafio.vendas.infrastructure.adapters.out.rest.dto.ReceitaFederalResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("receitaFederal")
public class ReceitaFederalAdapter implements CpfValidationStrategy {

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public ReceitaFederalAdapter(RestTemplate restTemplate,
            @Value("${services.receita-federal.url}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    @Override
    @CircuitBreaker(name = "receitaFederalService", fallbackMethod = "cpfValidationFallback")
    public CpfStatus validateCpf(String cpf) {
        return this.checkCpfWithReceitaFederalApi(cpf);
    }

    public CpfStatus cpfValidationFallback(String cpf, Throwable t) {
        try (Subsegment fallbackSubsegment = AWSXRay.beginSubsegment("Fallback:cpfValidation")) {
            log.warn(
                    "Fallback de validação de CPF acionado para o CPF: {}. Causa: {}. Retornando PENDENTE_VALIDACAO_RECEITA.",
                    cpf, t.getMessage());

            fallbackSubsegment.putAnnotation("cpf", cpf);
            fallbackSubsegment.putAnnotation("trigger_reason", t.getClass().getSimpleName());
            fallbackSubsegment.putMetadata("error_message", t.getMessage());
            fallbackSubsegment.addException(t);

            return CpfStatus.PENDENTE_VALIDACAO_RECEITA;
        }
    }

    private CpfStatus checkCpfWithReceitaFederalApi(String cpf) {
        String url = serviceUrl + "/" + cpf;

        try (Subsegment subsegment = AWSXRay.beginSubsegment("ExternalCall:ReceitaFederal")) {
            subsegment.putAnnotation("operation", "cpf_status_check");
            subsegment.putAnnotation("cpf", cpf);
            subsegment.putMetadata("request.url", url);

            log.info("Consultando status do CPF no serviço externo: {}", url);
            ReceitaFederalResponse response = restTemplate.getForObject(url, ReceitaFederalResponse.class);

            if (response == null || response.getStatus() == null || response.getStatus().isBlank()) {
                log.error("Resposta inválida (nula, vazia ou sem status) do serviço de CPF para o CPF: {}", cpf);
                throw new ExternalServiceContractException(
                        "Resposta inválida do serviço da Receita Federal.");
            }

            try {
                CpfStatus status = CpfStatus.valueOf(response.getStatus().toUpperCase().trim());
                subsegment.putMetadata("response.status", status.name());
                log.info("CPF {} validado com status: {}", cpf, status);
                return status;
            } catch (IllegalArgumentException e) {
                log.error("Status desconhecido '{}' recebido para o CPF: {}", response.getStatus(), cpf);
                throw new ExternalServiceContractException(
                        "Status '" + response.getStatus() + "' não é um valor esperado.", e);
            }

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("CPF {} não encontrado na base da Receita Federal. Considerado inválido.", cpf);
            return CpfStatus.CPF_INEXISTENTE;

        } catch (RestClientException e) {
            log.error("Erro de comunicação com o serviço de validação de CPF: {}", e.getMessage(), e);
            throw new ExternalServiceUnavailableException("Serviço de validação de CPF indisponível.", e);
        }
    }
}