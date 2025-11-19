package br.gov.caixa.painelinvestimentos.exception;

import java.io.InputStream;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("IllegalArgumentException deve retornar status 400 com detalhes")
    // Garante que IllegalArgumentException gera resposta 400 com mensagem e path corretos.
    void shouldHandleIllegalArgument() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rota");

        var response = handler.handleIllegalArgument(new IllegalArgumentException("erro"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("erro");
        assertThat(response.getBody().getPath()).isEqualTo("/rota");
    }

    @Test
    @DisplayName("Erros de validação devem incluir mapa de campos")
    // Verifica que MethodArgumentNotValidException retorna 400 com mapa de campos inválidos.
    void shouldHandleValidationErrors() throws Exception {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new Object(), "request");
        result.addError(new FieldError("request", "campo", "obrigatório"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, result);
        HttpServletRequest request = new MockHttpServletRequest("POST", "/rota");

        var response = handler.handleValidation(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getFields()).containsEntry("campo", "obrigatório");
    }

    @Test
    @DisplayName("Falhas de autenticação devem retornar 401 com mensagem clara")
    // Confirma que BadCredentialsException resulta em 401 com mensagem amigável.
    void shouldHandleAuthenticationErrors() {
        HttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");

        var response = handler.handleAuthentication(new BadCredentialsException("Credenciais inválidas"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).contains("Credenciais");
    }

    @Test
    @DisplayName("Falhas ao ler o corpo JSON devem gerar mensagem amigável")
    // Assegura que corpos ilegíveis retornam 400 com instruções úteis ao usuário.
    void shouldHandleUnreadableBody() {
        HttpServletRequest request = new MockHttpServletRequest("POST", "/simular-investimento");
        HttpInputMessage httpInputMessage = new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return InputStream.nullInputStream();
            }

            @Override
            public HttpHeaders getHeaders() {
                return HttpHeaders.EMPTY;
            }
        };
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("JSON parse error", new RuntimeException("detalhe"), httpInputMessage);

        var response = handler.handleUnreadableBody(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("Corpo");
    }

    @Test
    @DisplayName("Parâmetros com formato inválido devem orientar o usuário")
    // Verifica que parâmetros com tipo inválido geram 400 citando o campo afetado.
    void shouldHandleInvalidParameters() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/telemetria");
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException("a", Integer.class, "clienteId", null, new IllegalArgumentException("erro"));

        var response = handler.handleInvalidParameters(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("clienteId");
    }

    @Test
    @DisplayName("Sem dados para o perfil de risco deve retornar 404")
    // Confirma que ausência de dados de perfil devolve 404 mantendo a mensagem original.
    void shouldHandleMissingPerfil() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/perfil-risco/1");

        var response = handler.handleMissingPerfil(new SemDadosPerfilException("sem histórico"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("sem histórico");
    }

    @Test
    @DisplayName("Erros inesperados devem retornar 500")
    // Garante que exceções inesperadas retornam 500 preservando a mensagem do erro.
    void shouldHandleGenericErrors() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        org.mockito.Mockito.when(request.getRequestURI()).thenReturn("/rota");

        var response = handler.handleGeneric(new RuntimeException("falha"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("falha");
    }
}
