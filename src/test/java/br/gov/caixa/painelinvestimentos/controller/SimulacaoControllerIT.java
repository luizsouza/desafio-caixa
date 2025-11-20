package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.*;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.SimulacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimulacaoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private SimulacaoService simulacaoService;

    @Test
    @DisplayName("POST /simular-investimento deve validar payload e retornar dados da simulação")
    void shouldSimulateInvestmentViaController() throws Exception {
        SimularInvestimentoResponseDTO response = new SimularInvestimentoResponseDTO();
        ProdutoValidadoDTO produto = new ProdutoValidadoDTO();
        produto.setId(1L);
        produto.setNome("CDB Caixa 2026");
        produto.setTipo("CDB");
        response.setProdutoValidado(produto);
        ResultadoSimulacaoDTO resultado = new ResultadoSimulacaoDTO();
        resultado.setPrazoMeses(12);
        resultado.setValorFinal(5500.0);
        response.setResultadoSimulacao(resultado);
        response.setDataSimulacao(Instant.now());

        when(simulacaoService.simularInvestimento(any())).thenReturn(response);

        SimularInvestimentoRequestDTO request = new SimularInvestimentoRequestDTO();
        request.setClienteId(10L);
        request.setValor(5000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("CDB");

        mockMvc.perform(post("/simular-investimento")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.produtoValidado.nome").value("CDB Caixa 2026"))
                .andExpect(jsonPath("$.resultadoSimulacao.prazoMeses").value(12));
    }

    @Test
    @DisplayName("Payloads inválidos devem retornar erro de validação padronizado")
    void shouldValidateInvalidPayload() throws Exception {
        mockMvc.perform(post("/simular-investimento")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia deve encaminhar período para o serviço")
    void shouldForwardDateParameter() throws Exception {
        SimulacoesPorProdutoDiaDTO dto = new SimulacoesPorProdutoDiaDTO();
        dto.setProduto("CDB");
        dto.setData("2025-10-30");
        dto.setQuantidadeSimulacoes(1);
        dto.setMediaValorFinal(2000.0);

        when(simulacaoService.buscarSimulacoesPorProdutoPorDia(any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/simulacoes/por-produto-dia")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .param("inicio", "2025-10-01")
                        .param("fim", "2025-10-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].produto").value("CDB"));

        var inicioCaptor = org.mockito.ArgumentCaptor.forClass(LocalDate.class);
        var fimCaptor = org.mockito.ArgumentCaptor.forClass(LocalDate.class);
        verify(simulacaoService).buscarSimulacoesPorProdutoPorDia(inicioCaptor.capture(), fimCaptor.capture());
        assertThat(inicioCaptor.getValue()).isEqualTo(LocalDate.parse("2025-10-01"));
        assertThat(fimCaptor.getValue()).isEqualTo(LocalDate.parse("2025-10-30"));
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia sem parâmetros deve repassar nulos (usa período padrão no serviço)")
    void shouldForwardNullDatesWhenParamsOmitted() throws Exception {
        when(simulacaoService.buscarSimulacoesPorProdutoPorDia(any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/simulacoes/por-produto-dia")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin")))
                .andExpect(status().isOk());

        var inicioCaptor = org.mockito.ArgumentCaptor.forClass(LocalDate.class);
        var fimCaptor = org.mockito.ArgumentCaptor.forClass(LocalDate.class);
        verify(simulacaoService).buscarSimulacoesPorProdutoPorDia(inicioCaptor.capture(), fimCaptor.capture());
        assertThat(inicioCaptor.getValue()).isNull();
        assertThat(fimCaptor.getValue()).isNull();
    }

    @Test
    @DisplayName("Deve negar acesso sem header Authorization")
    void shouldRejectRequestsWithoutToken() throws Exception {
        mockMvc.perform(get("/simulacoes"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(simulacaoService);
    }

    @Test
    @DisplayName("Deve negar acesso com token inválido")
    void shouldRejectRequestsWithInvalidToken() throws Exception {
        mockMvc.perform(get("/simulacoes")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(simulacaoService);
    }
}
