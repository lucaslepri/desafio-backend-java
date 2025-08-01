package com.itau.desafio.vendas.infrastructure.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.desafio.vendas.domain.model.CpfStatus;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.CreateCustomerRequest;
import com.itau.desafio.vendas.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.MongoCustomerRepository;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.CustomerDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoCustomerRepository mongoCustomerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_BASE_URL = "/api/v1/customers";
    private CustomerDocument customer1;

    @BeforeEach
    void setUp() {
        mongoCustomerRepository.deleteAll();

        customer1 = CustomerDocument.builder()
                .id(UUID.randomUUID())
                .cpf("43991016877")
                .cpfStatus(CpfStatus.CPF_ATIVO)
                .fullName("José Rocha Ferro")
                .phoneNumber("11988887777")
                .monthlyIncome(new BigDecimal("10000"))
                .build();
        CustomerDocument customer2 = CustomerDocument.builder()
                .id(UUID.randomUUID())
                .cpf("10609584065")
                .cpfStatus(CpfStatus.CPF_ATIVO)
                .fullName("Joana Machado")
                .phoneNumber("22977776666")
                .monthlyIncome(new BigDecimal("15000"))
                .build();

        mongoCustomerRepository.saveAll(List.of(customer1, customer2));
    }

    @AfterEach
    void tearDown() {
        mongoCustomerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando usuário não está autenticado")
    void deveRetornarNaoAutorizado_quandoUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get(API_BASE_URL + "/{id}", customer1.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "agente.teste", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("POST /customers deve criar um cliente e retornar 201 Created")
    void deveCriarCliente_quandoDadosSaoValidos() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "New Customer", "44000593080", "11987654321", new BigDecimal("5000.00"));

        mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.fullName", is("New Customer")));
    }

    @Test
    @WithMockUser(username = "agente.teste", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("POST /customers deve retornar 400 Bad Request para nome em branco (validação de DTO)")
    void deveRetornarErro_quandoNomeEmBranco() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "  ", "44000593080", "11987654321", new BigDecimal("5000.00"));

        mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Erro de Validação de Dados de Entrada")))
                .andExpect(jsonPath("$.message", is("Um ou mais campos são inválidos. Verifique os detalhes.")))
                .andExpect(jsonPath("$.errors.fullName", is("O nome não pode estar em branco")));
    }

    @Test
    @WithMockUser(username = "agente.teste", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("POST /customers deve retornar 400 Bad Request para renda negativa (validação de DTO)")
    void deveRetornarErro_quandoRendaNegativa() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Negative User", "44000593080", "11987654321", new BigDecimal("-100"));

        mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Erro de Validação de Dados de Entrada")))
                .andExpect(jsonPath("$.message", is("Um ou mais campos são inválidos. Verifique os detalhes.")))
                .andExpect(jsonPath("$.errors.monthlyIncome", is("A renda mensal deve ser um valor positivo.")));
    }

    @Test
    @WithMockUser(username = "agente.teste", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("POST /customers deve retornar 422 Unprocessable Entity para CPF duplicado")
    void deveRetornarErro_quandoCpfDuplicado() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Duplicate User", customer1.getCpf(), "111111", BigDecimal.TEN);

        mockMvc.perform(post(API_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", is("Já existe um cliente cadastrado com o CPF fornecido.")));
    }

    @Test
    @WithMockUser(username = "agente.teste", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("GET /customers/{id} deve retornar cliente quando ID existe")
    void deveRetornarCliente_quandoIdExiste() throws Exception {
        mockMvc.perform(get(API_BASE_URL + "/{id}", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer1.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(customer1.getFullName())));
    }

    @Test
    @WithMockUser(username = "analista.teste", roles = {"ANALISTA_NEGOCIOS"})
    @DisplayName("PATCH /customers/{id} deve atualizar o cliente e retornar 200 OK")
    void deveAtualizarCliente_quandoDadosSaoValidos() throws Exception {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "José Rocha Ferro Atualizado", "11999998888", new BigDecimal("12000.00"));

        mockMvc.perform(patch(API_BASE_URL + "/{id}", customer1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer1.getId().toString())))
                .andExpect(jsonPath("$.fullName", is("José Rocha Ferro Atualizado")))
                .andExpect(jsonPath("$.phoneNumber", is("11999998888")))
                .andExpect(jsonPath("$.monthlyIncome", is(12000.00)));
    }

    @Test
    @WithMockUser(username = "analista.teste", roles = {"ANALISTA_NEGOCIOS"})
    @DisplayName("PATCH /customers/{id} deve retornar 404 Not Found quando ID não existe")
    void deveRetornarNotFound_quandoAtualizaClienteComIdInexistente() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Any Name", "123456789", BigDecimal.TEN);

        mockMvc.perform(patch(API_BASE_URL + "/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Cliente com ID " + nonExistentId + " não encontrado.")));
    }

    @Test
    @WithMockUser(username = "analista.teste", roles = {"ANALISTA_NEGOCIOS"})
    @DisplayName("PATCH /customers/{id} deve retornar 400 Bad Request para renda negativa")
    void deveRetornarErro_quandoAtualizaComRendaNegativa() throws Exception {
        UpdateCustomerRequest request = new UpdateCustomerRequest(
                "Updated Name", "11987654321", new BigDecimal("-500"));

        mockMvc.perform(patch(API_BASE_URL + "/{id}", customer1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Erro de Validação de Dados de Entrada")))
                .andExpect(jsonPath("$.message", is("Um ou mais campos são inválidos. Verifique os detalhes.")))
                .andExpect(jsonPath("$.errors.monthlyIncome", is("A renda mensal deve ser um valor positivo.")));
    }
    
    @Test
    @WithMockUser(username = "usuario.sem.role")
    @DisplayName("GET /customers deve retornar 403 Forbidden para usuário autenticado sem a role necessária")
    void deveRetornarForbidden_aoListarClientes_comUsuarioSemRole() throws Exception {
        mockMvc.perform(get(API_BASE_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "analista.invalido", roles = {"ANALISTA_NEGOCIOS"})
    @DisplayName("DELETE /customers/{id} deve retornar 403 Forbidden para usuário com role incorreta")
    void deveRetornarForbidden_aoDeletarCliente_comRoleIncorreta() throws Exception {
        mockMvc.perform(delete(API_BASE_URL + "/{id}", customer1.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "agente.invalido", roles = {"AGENTE_NEGOCIOS"})
    @DisplayName("PATCH /customers/{id} deve retornar 403 Forbidden para usuário com role incorreta")
    void deveRetornarForbidden_aoAtualizarCliente_comRoleIncorreta() throws Exception {
        UpdateCustomerRequest request = new UpdateCustomerRequest("Nome", "123", BigDecimal.TEN);
        mockMvc.perform(patch(API_BASE_URL + "/{id}", customer1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}