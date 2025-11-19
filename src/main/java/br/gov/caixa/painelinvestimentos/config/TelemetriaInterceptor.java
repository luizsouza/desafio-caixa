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

@Component
public class TelemetriaInterceptor implements HandlerInterceptor {

    private static final String INICIO_TELEMETRIA = "inicio_telemetria";
    private static final String MONITORAR_TELEMETRIA = "monitorar_telemetria";
    private static final PathPatternParser PATH_PARSER = new PathPatternParser();
    private static final List<PathPattern> ENDPOINTS_MONITORADOS = List.of(
            PATH_PARSER.parse("/auth/login"),
            PATH_PARSER.parse("/simular-investimento"),
            PATH_PARSER.parse("/simulacoes"),
            PATH_PARSER.parse("/simulacoes/por-produto-dia"),
            PATH_PARSER.parse("/health"),
            PATH_PARSER.parse("/produtos-recomendados/{perfil}"),
            PATH_PARSER.parse("/perfil-risco/{clienteId}"),
            PATH_PARSER.parse("/produtos"),
            PATH_PARSER.parse("/investimentos/{clienteId}")
    );

    private final TelemetriaService telemetriaService;

    public TelemetriaInterceptor(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!isEndpointMonitorado(request.getRequestURI())) {
            return true;
        }

        long inicio = System.currentTimeMillis();
        request.setAttribute(MONITORAR_TELEMETRIA, Boolean.TRUE);
        request.setAttribute(INICIO_TELEMETRIA, inicio);

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

        telemetriaService.registrarExecucao(request.getRequestURI(), duracao);
    }

    private boolean isEndpointMonitorado(String path) {
        PathContainer pathContainer = PathContainer.parsePath(path);
        return ENDPOINTS_MONITORADOS
                .stream()
                .anyMatch(pattern -> pattern.matches(pathContainer));
    }
}
