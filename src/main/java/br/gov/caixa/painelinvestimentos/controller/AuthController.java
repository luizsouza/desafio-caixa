package br.gov.caixa.painelinvestimentos.controller;

import br.gov.caixa.painelinvestimentos.model.dto.AuthRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.AuthResponseDTO;
import br.gov.caixa.painelinvestimentos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
        description = "Valida as credenciais enviadas e, em caso de sucesso, retorna um token JWT do tipo Bearer. "
                + "Observação: consulte o README do projeto para recuperar o usuário e a senha padrão de testes."
    )
    public ResponseEntity<AuthResponseDTO> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informe o usuário e senha divulgados no README antes de acionar o \"Try it out\".",
                required = true
            )
            @Valid @RequestBody AuthRequestDTO request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
