package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.config.OpenApiConfig;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.service.PerfilRiscoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(
    name = "Perfil de Risco",
    description = "Descubra rapidamente o perfil de risco calculado para cada cliente."
)
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SCHEME)
public class PerfilRiscoController {

    private final PerfilRiscoService perfilRiscoService;

    public PerfilRiscoController(PerfilRiscoService perfilRiscoService) {
        this.perfilRiscoService = perfilRiscoService;
    }

    @GetMapping("/perfil-risco/{clienteId}")
    @Operation(
        summary = "Consultar perfil",
        description = "Retorna o perfil de risco atual do cliente informado, pronto para ser exibido na jornada."
    )
    public ResponseEntity<PerfilRiscoResponseDTO> obterPerfilRisco(@PathVariable Long clienteId) {
        PerfilRiscoResponseDTO dto = perfilRiscoService.calcularPerfil(clienteId);
        return ResponseEntity.ok(dto);
    }
}
