package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(
        name = "Recomendações",
        description = "Sugestões de produtos alinhadas ao perfil calculado."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    public RecomendacaoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @GetMapping("/produtos-recomendados/{perfil}")
    @Operation(
            summary = "Recomendar por perfil",
            description = "Motor baseado em risco e rentabilidade. Retorna os produtos compatíveis ordenados pela pontuação.",
            responses = @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ProdutoRecomendadoDTO.class)))
    )
    public ResponseEntity<List<ProdutoRecomendadoDTO>> recomendarPorPerfil(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "Perfis disponiveis para teste.",
                    schema = @Schema(allowableValues = {"conservador", "moderado", "agressivo"}),
                    example = "moderado"
            )
            @PathVariable String perfil) {
        return ResponseEntity.ok(recomendacaoService.recomendarPorPerfil(perfil));
    }
}
