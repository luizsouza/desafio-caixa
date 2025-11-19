package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaPeriodoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaResponseDTO;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaServicoDTO;
import br.gov.caixa.painelinvestimentos.security.JwtService;
import br.gov.caixa.painelinvestimentos.service.TelemetriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetriaControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @MockBean
    private TelemetriaService telemetriaService;

    @Test
    @DisplayName("Telemetria deve devolver período e serviços com métricas")
    void shouldReturnTelemetryData() throws Exception {
        TelemetriaPeriodoDTO periodo = new TelemetriaPeriodoDTO();
        periodo.setInicio("2025-10-01");
        periodo.setFim("2025-10-30");
        TelemetriaServicoDTO servico = new TelemetriaServicoDTO();
        servico.setNome("/produtos");
        servico.setQuantidadeChamadas(3L);
        servico.setMediaTempoRespostaMs(120);

        TelemetriaResponseDTO response = new TelemetriaResponseDTO();
        response.setPeriodo(periodo);
        response.setServicos(List.of(servico));
        when(telemetriaService.obterTelemetria(any(), any())).thenReturn(response);

        mockMvc.perform(get("/telemetria")
                        .header("Authorization", "Bearer " + jwtService.generateToken("admin"))
                        .param("inicio", "2025-10-01")
                        .param("fim", "2025-10-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo.inicio").value("2025-10-01"))
                .andExpect(jsonPath("$.servicos[0].nome").value("/produtos"));

        ArgumentCaptor<LocalDate> inicioCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> fimCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(telemetriaService).obterTelemetria(inicioCaptor.capture(), fimCaptor.capture());
        assertThat(inicioCaptor.getValue()).isEqualTo(LocalDate.parse("2025-10-01"));
        assertThat(fimCaptor.getValue()).isEqualTo(LocalDate.parse("2025-10-30"));
    }
}
