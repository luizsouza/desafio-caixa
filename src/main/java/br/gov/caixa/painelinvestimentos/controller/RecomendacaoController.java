package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.service.RecomendacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(
    name = "Recomendações",
    description = "Sugestões de produtos alinhadas ao perfil informado."
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
        description = "Permite testar o motor de recomendação informando apenas o nome do perfil (ex.: 'moderado')."
    )
    public ResponseEntity<List<ProdutoRecomendadoDTO>> recomendarPorPerfil(
            @Parameter(
                in = ParameterIn.PATH,
                description = "Perfis disponiveis para teste. Escolha um deles antes de clicar em \"Try it out\".",
                schema = @Schema(allowableValues = {"conservador", "moderado", "agressivo"}),
                example = "moderado"
            )
            @PathVariable String perfil) {
        return ResponseEntity.ok(recomendacaoService.recomendarPorPerfil(perfil));
    }
}
