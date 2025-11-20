package br.gov.caixa.painelinvestimentos.config;

import br.gov.caixa.painelinvestimentos.service.TelemetriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TelemetriaInterceptorTest {

    private final TelemetriaService telemetriaService = mock(TelemetriaService.class);
    private final TelemetriaInterceptor interceptor = new TelemetriaInterceptor(telemetriaService);

    @Test
    @DisplayName("preHandle deve ignorar endpoints não monitorados")
    void shouldIgnoreUnmonitoredEndpoints() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/nao-monitorado");
        HttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        verifyNoInteractions(telemetriaService);
    }

    @Test
    @DisplayName("preHandle/afterCompletion devem registrar telemetria em endpoints monitorados")
    void shouldRecordTelemetryForMonitoredEndpoints() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, new Object());
        assertThat(proceed).isTrue();
        assertThat(request.getAttribute("monitorar_telemetria")).isEqualTo(Boolean.TRUE);

        // Simula tempo decorrido fixando o início
        request.setAttribute("inicio_telemetria", System.currentTimeMillis() - 50);
        interceptor.afterCompletion(request, response, new Object(), null);

        ArgumentCaptor<Long> duracao = ArgumentCaptor.forClass(Long.class);
        verify(telemetriaService).registrarExecucao(org.mockito.ArgumentMatchers.eq("/simulacoes"), duracao.capture());
        assertThat(duracao.getValue()).isGreaterThanOrEqualTo(0L);
    }
}
