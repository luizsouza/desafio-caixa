package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoResponseDTO;
import br.gov.caixa.painelinvestimentos.service.SimulacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulacoes")
@Tag(name = "Simulações", description = "Serviços de simulação de investimentos")
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @PostMapping
    @Operation(summary = "Realiza uma simulação de investimento")
    public ResponseEntity<SimulacaoResponseDTO> simular(@Valid @RequestBody SimulacaoRequestDTO request) {
        SimulacaoResponseDTO response = simulacaoService.simular(request);
        return ResponseEntity.ok(response);
    }
}
