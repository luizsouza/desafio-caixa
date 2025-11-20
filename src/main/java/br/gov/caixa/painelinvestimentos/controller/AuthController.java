package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.AuthRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.AuthResponseDTO;
import br.gov.caixa.painelinvestimentos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Autenticação",
        description = "Fluxo de login responsável por emitir tokens JWT para os demais serviços."
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autentica o usuário",
            description = """
                    Valida as credenciais enviadas e retorna um token JWT do tipo Bearer.
                    Credenciais para avaliação:
                    {
                      "username": "admin",
                      "password": "senha123"
                    }
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                            content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciais invalidas")
            }
    )
    public ResponseEntity<AuthResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informe o usuário e senha indicados acima antes do \"Try it out\".",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequestDTO.class)))
            @Valid @RequestBody AuthRequestDTO request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
