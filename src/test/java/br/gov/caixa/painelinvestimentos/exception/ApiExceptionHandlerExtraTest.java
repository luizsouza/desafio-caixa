package br.gov.caixa.painelinvestimentos.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.time.format.DateTimeParseException;
import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerExtraTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    @DisplayName("Corpo com formato inválido deve apontar o campo com problema")
    void shouldHandleUnreadableBodyWithField() {
        HttpServletRequest request = new MockHttpServletRequest("POST", "/simular-investimento");
        InvalidFormatException cause = InvalidFormatException.from(null, "bad number", "abc", String.class);
        cause.prependPath((Object) null, "valor");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("erro", cause, null);

        var response = handler.handleUnreadableBody(ex, request);

        assertThat(response.getBody().getMessage()).contains("valor").contains("abc");
    }

    @Test
    @DisplayName("DateTimeParseException deve retornar mensagem usando valor enviado")
    void shouldHandleDateTimeParse() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes/por-produto-dia");
        DateTimeParseException ex = new DateTimeParseException("erro", "20-11-2025", 2);

        var response = handler.handleInvalidParameters(ex, request);

        assertThat(response.getBody().getMessage()).contains("20-11-2025");
    }

    @Test
    @DisplayName("BindException deve citar o campo rejeitado")
    void shouldHandleBindException() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/telemetria");
        BindException bind = new BindException(new Object(), "req");
        bind.addError(new FieldError("req", "prazo", "bad"));

        var response = handler.handleInvalidParameters(bind, request);

        assertThat(response.getBody().getMessage()).contains("prazo");
    }
}
