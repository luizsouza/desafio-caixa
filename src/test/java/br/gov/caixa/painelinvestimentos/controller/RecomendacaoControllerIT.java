package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.RecomendacaoService;
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
class RecomendacaoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private RecomendacaoService recomendacaoService;

    @Test
    @DisplayName("Endpoint deve responder recomendações compatíveis com o perfil informado")
    void shouldReturnRecommendationsByProfile() throws Exception {
        ProdutoRecomendadoDTO dto = new ProdutoRecomendadoDTO();
        dto.setId(1L);
        dto.setNome("CDB Conservador");
        dto.setTipo("CDB");
        dto.setRisco("BAIXO");
        dto.setRentabilidade(0.8);

        when(recomendacaoService.recomendarPorPerfil("moderado"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/produtos-recomendados/moderado")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("CDB Conservador"));
    }
}
