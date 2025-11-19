package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.TelemetriaEntity;
import br.gov.caixa.painelinvestimentos.repository.TelemetriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetriaServiceTest {

    @Mock
    private TelemetriaRepository telemetriaRepository;

    @InjectMocks
    private TelemetriaService telemetriaService;

    @Test
    @DisplayName("registrarExecucao deve enviar entidade completa para o repositório")
    void shouldPersistTelemetryEntries() {
        telemetriaService.registrarExecucao("/produtos", 120L);

        ArgumentCaptor<TelemetriaEntity> captor = ArgumentCaptor.forClass(TelemetriaEntity.class);
        verify(telemetriaRepository).save(captor.capture());

        TelemetriaEntity salvo = captor.getValue();
        assertThat(salvo.getEndpoint()).isEqualTo("/produtos");
        assertThat(salvo.getTempoRespostaMs()).isEqualTo(120L);
        assertThat(salvo.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("obterTelemetria deve agrupar estatísticas por endpoint")
    void shouldAggregateTelemetry() {
        LocalDate inicio = LocalDate.parse("2025-10-01");
        LocalDate fim = LocalDate.parse("2025-10-10");

        when(telemetriaRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of(
                        registro("/produtos", 100),
                        registro("/produtos", 200),
                        registro("/simular", 50)
                ));

        TelemetriaResponseDTO resposta = telemetriaService.obterTelemetria(inicio, fim);

        assertThat(resposta.getPeriodo().getInicio()).isEqualTo("2025-10-01");
        assertThat(resposta.getServicos()).hasSize(2);

        assertThat(resposta.getServicos())
                .anySatisfy(dto -> {
                    if (dto.getNome().equals("/produtos")) {
                        assertThat(dto.getQuantidadeChamadas()).isEqualTo(2);
                        assertThat(dto.getMediaTempoRespostaMs()).isEqualTo(150);
                    }
                });
    }

    @Test
    @DisplayName("Quando datas não são enviadas deve considerar a janela padrão de 30 dias")
    void shouldUseDefaultWindowWhenDatesMissing() {
        when(telemetriaRepository.findByTimestampBetween(any(), any()))
                .thenReturn(List.of());

        telemetriaService.obterTelemetria(null, null);

        ArgumentCaptor<LocalDateTime> captorInicio = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> captorFim = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(telemetriaRepository).findByTimestampBetween(captorInicio.capture(), captorFim.capture());

        long dias = java.time.Duration.between(captorInicio.getValue(), captorFim.getValue()).toDays();
        assertThat(dias).isEqualTo(30);
    }

    private TelemetriaEntity registro(String endpoint, long tempo) {
        TelemetriaEntity entity = new TelemetriaEntity();
        entity.setEndpoint(endpoint);
        entity.setTempoRespostaMs(tempo);
        entity.setTimestamp(LocalDateTime.now());
        return entity;
    }
}
