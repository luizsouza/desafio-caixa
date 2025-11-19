package br.gov.caixa.painelinvestimentos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(
    name = "Status da API",
    description = "Endpoint simples usado pelo monitoramento para confirmar que o serviço está de pé."
)
public class HealthController {

    @GetMapping("/health")
    @Operation(
        summary = "Consulta de status",
        description = "Retorna um objeto com o estado atual e o timestamp para facilitar integrações de health-check."
    )
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "ok");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(body);
    }
}
