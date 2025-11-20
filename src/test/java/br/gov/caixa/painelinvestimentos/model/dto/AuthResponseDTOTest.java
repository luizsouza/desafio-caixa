package br.gov.caixa.painelinvestimentos.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseDTOTest {

    @Test
    @DisplayName("AuthResponseDTO deve manter token e tipo padrão")
    void shouldKeepTokenAndDefaultType() {
        AuthResponseDTO dto = new AuthResponseDTO("abc");

        assertThat(dto.getToken()).isEqualTo("abc");
        assertThat(dto.getTipo()).isEqualTo("Bearer");

        dto.setTipo("Custom");
        dto.setToken("xyz");

        assertThat(dto.getToken()).isEqualTo("xyz");
        assertThat(dto.getTipo()).isEqualTo("Custom");
    }
}
