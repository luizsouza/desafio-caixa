package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ProdutoService produtoService;

    @Test
    @DisplayName("Requisições autenticadas devem listar produtos")
    void shouldListProductsWhenAuthenticated() throws Exception {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("CDB Caixa 2026");
        dto.setTipo("CDB");
        dto.setRentabilidade(0.12);
        dto.setRisco("BAIXO");

        when(produtoService.listarTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/produtos")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("CDB Caixa 2026"))
                .andExpect(jsonPath("$[0].rentabilidade").value(0.12));
    }

    @Test
    @DisplayName("Sem token a API deve negar acesso")
    void shouldRejectWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isForbidden());
    }
}
