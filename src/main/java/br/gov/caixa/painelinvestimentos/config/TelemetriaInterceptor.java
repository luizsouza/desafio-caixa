package br.gov.caixa.painelinvestimentos.config;

import br.gov.caixa.painelinvestimentos.service.TelemetriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.Optional;

@Component
public class TelemetriaInterceptor implements HandlerInterceptor {

    private static final String INICIO_TELEMETRIA = "inicio_telemetria";
    private static final String MONITORAR_TELEMETRIA = "monitorar_telemetria";
    private static final String ENDPOINT_NORMALIZADO = "endpoint_normalizado";
    private static final PathPatternParser PATH_PARSER = new PathPatternParser();
    private static final List<EndpointMonitorado> ENDPOINTS_MONITORADOS = List.of(
            endpoint("/auth/login", "/auth/login"),
            endpoint("/simular-investimento", "/simular-investimento"),
            endpoint("/simulacoes", "/simulacoes"),
            endpoint("/simulacoes/por-produto-dia", "/simulacoes/por-produto-dia"),
            endpoint("/health", "/health"),
            endpoint("/produtos-recomendados/{perfil}", "/produtos-recomendados"),
            endpoint("/perfil-risco/{clienteId}", "/perfil-risco"),
            endpoint("/produtos", "/produtos"),
            endpoint("/investimentos/{clienteId}", "/investimentos")
    );

    private final TelemetriaService telemetriaService;

    public TelemetriaInterceptor(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Optional<EndpointMonitorado> endpoint = findEndpointMonitorado(request.getRequestURI());
        if (endpoint.isEmpty()) {
            return true;
        }

        long inicio = System.currentTimeMillis();
        request.setAttribute(MONITORAR_TELEMETRIA, Boolean.TRUE);
        request.setAttribute(INICIO_TELEMETRIA, inicio);
        request.setAttribute(ENDPOINT_NORMALIZADO, endpoint.get().canonical());

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        if (!Boolean.TRUE.equals(request.getAttribute(MONITORAR_TELEMETRIA))) {
            return;
        }

        Long inicio = (Long) request.getAttribute(INICIO_TELEMETRIA);
        if (inicio == null) return;

        long fim = System.currentTimeMillis();
        long duracao = fim - inicio;

        String endpoint = (String) request.getAttribute(ENDPOINT_NORMALIZADO);
        telemetriaService.registrarExecucao(endpoint != null ? endpoint : request.getRequestURI(), duracao);
    }

    private Optional<EndpointMonitorado> findEndpointMonitorado(String path) {
        PathContainer pathContainer = PathContainer.parsePath(path);
        return ENDPOINTS_MONITORADOS.stream()
                .filter(e -> e.pattern().matches(pathContainer))
                .findFirst();
    }

    private static EndpointMonitorado endpoint(String pattern, String canonical) {
        return new EndpointMonitorado(PATH_PARSER.parse(pattern), canonical);
    }

    private record EndpointMonitorado(PathPattern pattern, String canonical) {}
}
