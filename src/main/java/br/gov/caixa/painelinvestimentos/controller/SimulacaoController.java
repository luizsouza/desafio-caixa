package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacoesPorProdutoDiaDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoResponseDTO;
import br.gov.caixa.painelinvestimentos.service.SimulacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(
        name = "Simulacoes",
        description = "Executa novas simulacoes e consulta metricas para apoiar o time de produtos."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping("/simular-investimento")
    @Operation(
            summary = "Nova simulacao",
            description = "Processa os dados do cliente e retorna um cenario completo de investimento."
    )
    public ResponseEntity<SimularInvestimentoResponseDTO> simularInvestimento(
            @Valid @RequestBody SimularInvestimentoRequestDTO request) {

        return ResponseEntity.ok(simulacaoService.simularInvestimento(request));
    }

    @GetMapping("/simulacoes")
    @Operation(
            summary = "Historico de simulacoes",
            description = "Traz as ultimas simulacoes realizadas para que analistas possam acompanhar o uso."
    )
    public ResponseEntity<List<SimulacaoHistoricoDTO>> listar() {
        return ResponseEntity.ok(simulacaoService.listarHistorico());
    }

    @GetMapping("/simulacoes/por-produto-dia")
    @Operation(
            summary = "Metricas por produto",
            description = "Mostra quantas simulacoes cada produto recebeu em um dia especifico (formato AAAA-MM-DD)."
    )
    public ResponseEntity<List<SimulacoesPorProdutoDiaDTO>> porProdutoDia(
            @RequestParam(required = false)
            @Parameter(description = "Data inicial no formato AAAA-MM-DD.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,
            @RequestParam(required = false)
            @Parameter(description = "Data final no formato AAAA-MM-DD.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim) {

        return ResponseEntity.ok(simulacaoService.buscarSimulacoesPorProdutoPorDia(inicio, fim));
    }
}
