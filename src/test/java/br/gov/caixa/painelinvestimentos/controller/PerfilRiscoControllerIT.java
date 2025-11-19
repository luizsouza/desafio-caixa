package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.PerfilRiscoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PerfilRiscoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private PerfilRiscoService perfilRiscoService;

    @Test
    @DisplayName("Perfil de risco deve trazer pontuação e descrição")
    void shouldReturnRiskProfile() throws Exception {
        PerfilRiscoResponseDTO dto = new PerfilRiscoResponseDTO();
        dto.setClienteId(999L);
        dto.setPerfil(PerfilRisco.MODERADO);
        dto.setPontuacaoTotal(55);
        dto.setDescricao("Moderado");

        when(perfilRiscoService.calcularPerfil(999L)).thenReturn(dto);

        mockMvc.perform(get("/perfil-risco/999")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("MODERADO"))
                .andExpect(jsonPath("$.pontuacaoTotal").value(55));
    }
}
