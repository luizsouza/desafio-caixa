package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaResponseDTO;
import br.gov.caixa.painelinvestimentos.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/telemetria")
@Tag(
        name = "Telemetria",
        description = "Painel com volume de chamadas e tempo medio de resposta por endpoint."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @GetMapping
    @Operation(
            summary = "Métricas de uso",
            description = """
                    Informa quantas chamadas cada serviço recebeu e o tempo médio de resposta.
                    Início/fim sao opcionais (formato AAAA-MM-DD); se não forem informados, retorna os últimos 30 dias.
                    """,
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TelemetriaResponseDTO.class)))
    )
    public ResponseEntity<TelemetriaResponseDTO> obterTelemetria(
            @RequestParam(required = false)
            @Parameter(description = "Data inicial no formato AAAA-MM-DD.", example = "2025-10-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @Parameter(description = "Data final no formato AAAA-MM-DD.", example = "2025-12-31")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {

        TelemetriaResponseDTO response = telemetriaService.obterTelemetria(inicio, fim);
        return ResponseEntity.ok(response);
    }
}
