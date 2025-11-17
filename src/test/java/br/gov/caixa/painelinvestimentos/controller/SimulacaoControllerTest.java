package br.gov.caixa.painelinvestimentos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SimulacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarSimulacaoComSucesso() throws Exception {
        String body = """
            {
              "clienteId": 123,
              "produtoId": 1,
              "valorInvestido": 1000.0,
              "prazoMeses": 12
            }
            """;

        mockMvc.perform(post("/api/simulacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(123))
                .andExpect(jsonPath("$.produto.id").value(1))
                .andExpect(jsonPath("$.valorInvestido").value(1000.0))
                .andExpect(jsonPath("$.valorFinal", greaterThan(1000.0)));
    }

    @Test
    void deveRetornar400QuandoProdutoNaoExiste() throws Exception {
        String body = """
            {
              "clienteId": 1,
              "produtoId": 9999,
              "valorInvestido": 1000.0,
              "prazoMeses": 12
            }
            """;

        mockMvc.perform(post("/api/simulacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message",
                        startsWith("Produto não encontrado")))
                .andExpect(jsonPath("$.path").value("/api/simulacoes"));
    }

    @Test
    void deveRetornar400QuandoRequestEhInvalido() throws Exception {
        // falta valorInvestido
        String body = """
            {
              "clienteId": 1,
              "produtoId": 1,
              "prazoMeses": 12
            }
            """;

        mockMvc.perform(post("/api/simulacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.fields.valorInvestido", notNullValue()))
                .andExpect(jsonPath("$.path").value("/api/simulacoes"));
    }
}
