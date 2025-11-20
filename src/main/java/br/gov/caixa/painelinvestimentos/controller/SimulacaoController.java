package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacoesPorProdutoDiaDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoResponseDTO;
import br.gov.caixa.painelinvestimentos.service.SimulacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        name = "Simulações",
        description = "Executa novas simulações, lista históricos e expõe métricas diárias por produto."
)
// Exige que todas as chamadas para este controller incluam um token JWT válido.
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping("/simular-investimento")
    @Operation(
            summary = "Nova simulação",
            description = "Processa os dados do cliente e retorna o produto validado e o resultado da simulação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Simulação realizada",
                            content = @Content(schema = @Schema(implementation = SimularInvestimentoResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Payload inválido"),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado para o tipo informado")
            }
    )
    public ResponseEntity<SimularInvestimentoResponseDTO> simularInvestimento(
            @Valid @RequestBody SimularInvestimentoRequestDTO request) {

        return ResponseEntity.ok(simulacaoService.simularInvestimento(request));
    }

    @GetMapping("/simulacoes")
    @Operation(
            summary = "Histórico de simulações",
            description = "Retorna todas as simulações executadas ordenadas da mais recente para a mais antiga.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SimulacaoHistoricoDTO.class)))
    )
    public ResponseEntity<List<SimulacaoHistoricoDTO>> listar() {
        return ResponseEntity.ok(simulacaoService.listarHistorico());
    }

    @GetMapping("/simulacoes/por-produto-dia")
    @Operation(
            summary = "Métricas por produto e dia",
            description = """
                    Consolida as simulações por produto em cada dia do período informado.
                    Se não forem enviados parâmetros, retorna automaticamente os últimos 30 dias.
                    """,
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SimulacoesPorProdutoDiaDTO.class)))
    )
    public ResponseEntity<List<SimulacoesPorProdutoDiaDTO>> porProdutoDia(
            @RequestParam(required = false)
            @Parameter(description = "Data inicial (AAAA-MM-DD). Se ausente, usa 30 dias antes da data final.",
                    example = "2025-10-01")
            // A anotação @DateTimeFormat garante que o Spring consiga converter a string do request para um objeto LocalDate.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,
            @RequestParam(required = false)
            @Parameter(description = "Data final (AAAA-MM-DD). Se ausente, usa a data atual.",
                    example = "2025-12-31")
            // O padrão ISO.DATE (AAAA-MM-DD) usado para evitar problemas com formatos de data locais.
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim) {

        return ResponseEntity.ok(simulacaoService.buscarSimulacoesPorProdutoPorDia(inicio, fim));
    }
}
