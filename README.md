# Desafio Técnico - Engenharia de Software

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)
![AWS](https://img.shields.io/badge/AWS-ECS%20|%20S3%20|%20ECR-orange.svg)
![Terraform](https://img.shields.io/badge/Terraform-IaC-blueviolet.svg)
![CD](https://img.shields.io/badge/CI/CD-GitHub%20Actions-lightgrey.svg)

Proposta de implementação do desafio técnico para engenheiros de Software.


O negócio escolhido foi o de venda de veículos financiados.

Para isto, idealizei um sistema de gerenciamento de propostas de financiamento de veículos, construído com microserviços, boas práticas de desenvolvimento, automação e observabilidade.

## Sumário

- [Visão Geral da Solução](#visão-geral-da-solução)
- [Diagramas ](#diagramas)
  - [Contexto](#contexto)
  - [Containers](#containers)
  - [Diagramas de Sequência](#diagramas-de-sequência)
- [Diferenciais Implementados](#diferenciais-implementados)
- [Tecnologias e Padrões](#tecnologias-e-padrões)
- [Reflexões, melhorias e gaps](#reflexões-melhorias-e-gaps)
- [Links](#links)

## Visão Geral da Solução

A solução foi projetada como um sistema de microserviços.

- **`ms-proposals`**: Serviço principal responsável pela gestão de clientes e propostas (operações CRUD). Idealmente, seria decomposto em microserviços separados.
- **`ms-auditoria`**: Serviço secundário que implementa a feature de auditoria. Escuta em tempo real por alterações no banco de dados (`MongoDB Change Streams`) e exporta um registro, um arquivo JSON, de cada modificação para um repositório no AWS S3.

## Diagramas

Contexto

Iterações do sistema com usuários e sistemas externos.

![Contexto](/docs/1.Contexto%20do%20Sistema%20de%20Vendas.png)

Containers

Este diagrama mostra os blocos de construção da solução dentro da AWS, os microserviços, o banco de dados e os serviços da AWS utilizados.

![Containers](/docs/2.Containers%20do%20Sistema%20de%20Vendas.png)

![ms-proposals](/docs/3.Componentes%20do%20ms-proposals.png)

![ms-auditoria](/docs/4.Componentes%20do%20ms-auditoria.png)

### Diagramas de Sequência

**Cenário 1: Criação de Cliente com Validação de CPF na Receita Federal(Caminho Feliz)**
![Sequência - Criação de Cliente](/docs/5caminho-feliz-cpf-receita.png)

**Cenário 2: Mantém a criação de clientes mesmo quando o provedor Receita Federal está indisponível. Circuit Breaker**
O cadastro continua, mas com um status `PENDENTE_VALIDACAO_RECEITA`.
![Sequência - Circuit Breaker Aberto](/docs/8.caminho-feliz-cpf-receita-circuit-aberto.png)

**Cenário 3: Feature Toggle para Provedor de CPF**
Semelhante ao cenário 1, mas com o uso de um provedor de validaão alternativo (`JatoCpfServicesAdapter`).
A virada entre provedores pode ser controlada LIVE com Feature Toggle.
![Sequência - Feature Toggle](/docs/7.caminho-feliz-cpf-jatocpf.png)

Exemplo de JSON de auditoria gerado pelo `ms-auditoria`:
```json
{
  "operationType" : "UPDATED",
  "eventTimestamp" : "2025-07-28T03:27:41",
  "databaseName" : "reembolsos",
  "collectionName" : "customers",
  "changedBy" : "admin",
  "documentKey" : {
    "_id" : {
      "type" : 3,
      "data" : "jEcxeNkIciOC10GSGS+fkw=="
    }
  },
  "documentBefore" : {
    "_id" : "237208d9-7831-478c-939f-2f199241d782",
    "cpf" : "37991016877",
    "fullName" : "Jorginho da Silva",
    "phoneNumber" : "11987654321",
    "monthlyIncome" : "5000.00",
    "createdAt" : "2025-07-27T21:35:13.664+00:00",
    "lastModifiedAt" : "2025-07-27T21:35:13.679+00:00",
    "lastModifiedBy" : "admin",
    "isNew" : false,
    "_class" : "com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument"
  },
  "documentAfter" : {
    "_id" : "237208d9-7831-478c-939f-2f199241d782",
    "cpf" : "37991016877",
    "fullName" : "Jorgi2nho da Silva",
    "phoneNumber" : "11987654321",
    "monthlyIncome" : "5000.00",
    "createdAt" : "2025-07-27T21:35:13.664+00:00",
    "lastModifiedAt" : "2025-07-27T21:35:13.679+00:00",
    "lastModifiedBy" : "admin",
    "isNew" : false,
    "_class" : "com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument"
  }
}

```

-   **Desenho de Solução na AWS**: ECS Fargate, ALB, ECR, S3, Secrets Manager e Parameter Store.
-   **Infraestrutura como Código (IaC)**: **Terraform**. O código está na pasta [`/infra`](./infra).
-   *CI/CD com GitHub Actions**: Pipelines de deploy contínuo foram criados para os dois microserviços.
-   **Observabilidade**:
    -   **Logs Estruturados**: Configuração de Logback para gerar logs em formato JSON.
    -   **Métricas**: Exposição de métricas da aplicação via Spring Actuator e Micrometer (também de negócio).
    -   *Tracing Distribuido**: Integração com **AWS X-Ray** (X-Ray como sidecar dos dos containers).
-   **Resiliência**: **Circuit Breaker** no `ReceitaFederalAdapter` para manter o cadastro operacional em caso de falha no serivço da Receita.
-   **Segurança**: Autenticação e autorização (ambas ad-hoc - realista) com roles.
-   **Feature Toggle**: Há uma flag de configuração (`cpf.validator.provider`) gerenciada via **AWS Parameter Store** para trocar entre diferentes implementações da `CpfValidationStrategy` em hot.

## Tecnologias e Padrões

-   **Linguagem**: Java 21
-   **Framework**: Spring Boot 3.5.4, Spring Data, Spring Security, Spring Cloud AWS
-   **Arquitetura**: Microservices, Hexagonal (Ports and Adapters)
-   **Banco de Dados**: MongoDB
-   **Infraestrutura**: AWS (ECS Fargate, ALB, ECR, S3, IAM, VPC, Secrets Manager, Parameter Store)
-   **Terraform**
-   **CI/CD**: GitHub Actions, Docker
-   **Resilience4j (Circuit Breaker)**
-   **Observabilidade**: Micrometer, AWS X-Ray
-   **API**: REST, HATEOAS, Swagger

Existe um arquivo do VS Code Rest Client (.http) em /ms/proposals/utils com algumas chamadas de exemplo.

### Reflexões, melhorias e gaps.
- Testes: adicionar mais testes unitários, de integração e arquitetura. Implementar testes de contrato.
Segurança: Configurar HTTPS no ALB, utilizar WAF, e reavaliar políticas no IAM.
- Cache Distribuído: Substituir o cache Caffeine por algo como Redis ou ElastiCache.

- ms-auditoria: precisa de alternativa caso o S3 caia, como uma DLQ.
Acoplar direto no banco garante que somente as alterações de fato consolidadas sejam "auditadas".
Seguindo assim, e sem considerar custo, o uso de Kafka + Dezebium (eu tentei, mas ficaria caro) seria o mais out-of-the-box.
A proposta nesta entrega é uma alternativa low cost, que poderia evoluir para usar algum tipo de particionamento e algoritmo de coordenação distribuida para dividr horizontalmente as cargas. Seria robusto, mas ainda com risco de perder eventos por um mínimo intervalo.

- ainda sobre o ms-auditoria: É nítido e necessário o desacoplamento entre a escuta e a persistência das mudanças .
Outra alternativa seria a "notificação" das alterações saírem dos próprios microserviços que são resposáveis, mas com o trade-offs de acomplamento, extrapolamento da responsabilidade do ms-proposal, e risco de gravar mudanças que não foram efetivamente persistidas.


### Links:
Swagger [http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com/proposals/swagger-ui/index.html](http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com/proposals/swagger-ui/index.html)<br>
Prometheus [http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com/proposals/actuator/prometheus](http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com)<br>
Health [http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com/proposals/actuator/health](http://desafio-itau-alb-889643146.us-west-2.elb.amazonaws.com/proposals/actuator/health)
