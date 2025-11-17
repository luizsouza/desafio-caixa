package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Recomendações", description = "Motor de recomendação de investimentos")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    public RecomendacaoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @GetMapping("/{clienteId}/recomendacoes")
    @Operation(summary = "Retorna os produtos recomendados para o cliente")
    public ResponseEntity<List<ProdutoRecomendadoDTO>> recomendar(@PathVariable Long clienteId) {
        return ResponseEntity.ok(recomendacaoService.recomendar(clienteId));
    }
}