package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.InvestimentoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.InvestimentoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InvestimentoControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private InvestimentoService investimentoService;

    @Test
    @DisplayName("Histórico deve responder dados formatados corretamente")
    void shouldReturnHistory() throws Exception {
        InvestimentoHistoricoDTO dto = new InvestimentoHistoricoDTO();
        dto.setId(1L);
        dto.setTipo("CDB");
        dto.setValor(1000.0);
        dto.setRentabilidade(0.12);
        dto.setData(LocalDate.parse("2025-01-01"));

        when(investimentoService.listarPorCliente(123L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/investimentos/123")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("CDB"))
                .andExpect(jsonPath("$[0].data").value("2025-01-01"));

        verify(investimentoService).listarPorCliente(123L);
    }
}
