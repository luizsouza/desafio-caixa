package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.InvestimentoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.service.InvestimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
    name = "Investimentos",
    description = "Recupera o histórico de aplicações e movimentações financeiras do cliente."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class InvestimentoController {

    private final InvestimentoService investimentoService;

    public InvestimentoController(InvestimentoService investimentoService) {
        this.investimentoService = investimentoService;
    }

    @GetMapping("/investimentos/{clienteId}")
    @Operation(
        summary = "Listar histórico",
        description = "Retorna as operações de investimento do cliente, já prontas para exibição em extratos."
    )
    public ResponseEntity<List<InvestimentoHistoricoDTO>> listar(@PathVariable Long clienteId) {
        return ResponseEntity.ok(investimentoService.listarPorCliente(clienteId));
    }
}
