package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaResponseDTO;
import br.gov.caixa.painelinvestimentos.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/telemetria")
@Tag(
    name = "Telemetria",
    description = "Painel de acompanhamento do uso da API com filtros por período no formato AAAA-MM-DD."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    /**
     * GET /telemetria
     *
     * Comportamento:
     * - Se início e fim forem informados → usa os dois
     * - Se nenhum for informado → últimos 30 dias
     * - Se informar somente início ou somente fim → últimos 30 dias
     *
     */
    
    @GetMapping
    @Operation(
        summary = "Métricas de uso",
        description = "Informe datas no padrão AAAA-MM-DD (ex.: 2025-10-30). Sem filtros, retornamos os últimos 30 dias."
    )
    public ResponseEntity<TelemetriaResponseDTO> obterTelemetria(
            @RequestParam(required = false)
            @Parameter(description = "Data inicial no formato AAAA-MM-DD.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @Parameter(description = "Data final no formato AAAA-MM-DD.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {

        TelemetriaResponseDTO response = telemetriaService.obterTelemetria(inicio, fim);
        return ResponseEntity.ok(response);
    }
}
