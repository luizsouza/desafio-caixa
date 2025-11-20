package br.gov.caixa.painelinvestimentos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    @DisplayName("Construtor deve inicializar timestamp e campos básicos")
    void shouldInitializeWithTimestamp() {
        ApiError error = new ApiError(400, "Bad Request", "msg", "/rota");

        assertThat(Instant.parse(error.getTimestamp())).isNotNull();
        assertThat(error.getStatus()).isEqualTo(400);
        assertThat(error.getError()).isEqualTo("Bad Request");
        assertThat(error.getMessage()).isEqualTo("msg");
        assertThat(error.getPath()).isEqualTo("/rota");
    }

    @Test
    @DisplayName("Getters e setters devem refletir alterações")
    void shouldSetAndGetFields() {
        ApiError error = new ApiError();
        error.setTimestamp("ts");
        error.setStatus(401);
        error.setError("Unauthorized");
        error.setMessage("fail");
        error.setPath("/auth/login");
        error.setFields(Map.of("campo", "invalido"));

        assertThat(error.getTimestamp()).isEqualTo("ts");
        assertThat(error.getStatus()).isEqualTo(401);
        assertThat(error.getError()).isEqualTo("Unauthorized");
        assertThat(error.getMessage()).isEqualTo("fail");
        assertThat(error.getPath()).isEqualTo("/auth/login");
        assertThat(error.getFields()).containsEntry("campo", "invalido");
    }
}
