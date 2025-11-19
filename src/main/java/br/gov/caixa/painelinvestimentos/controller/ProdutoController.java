package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
    name = "Produtos",
    description = "Catálogo completo de produtos elegíveis para recomendação e simulação."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/produtos")
    @Operation(
        summary = "Catálogo de produtos",
        description = "Lista todos os produtos com informações essenciais para popular telas e combos."
    )
    public ResponseEntity<List<ProdutoDTO>> listar() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }
}
