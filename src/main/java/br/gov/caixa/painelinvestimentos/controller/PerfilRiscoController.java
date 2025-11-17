package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.service.PerfilRiscoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Perfil de Risco", description = "Avaliação de perfil de risco do cliente")
public class PerfilRiscoController {

    private final PerfilRiscoService perfilRiscoService;

    public PerfilRiscoController(PerfilRiscoService perfilRiscoService) {
        this.perfilRiscoService = perfilRiscoService;
    }

    @GetMapping("/{clienteId}/perfil-risco")
    @Operation(
            summary = "Consulta o perfil de risco de um cliente",
            description = "Calcula o perfil de risco com base no histórico de simulações do cliente."
    )
    public ResponseEntity<PerfilRiscoResponseDTO> obterPerfilRisco(@PathVariable Long clienteId) {
        PerfilRiscoResponseDTO dto = perfilRiscoService.calcularPerfil(clienteId);
        return ResponseEntity.ok(dto);
    }
}