package br.gov.caixa.painelinvestimentos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecomendacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarRecomendacoesParaCliente() throws Exception {
        // clienteId = 1L (não precisa existir na base; PerfilRiscoService trata lista vazia como conservador)

        mockMvc.perform(get("/api/clientes/1/recomendacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].nome", not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].tipo", not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].risco", not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].rentabilidade", notNullValue()));
    }
}