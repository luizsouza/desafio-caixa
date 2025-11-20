package br.gov.caixa.painelinvestimentos.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TelemetriaEntityTest {

    @Test
    @DisplayName("Construtor completo deve popular campos e getters/setters devem retornar valores")
    void shouldPopulateFieldsViaConstructorAndSetters() {
        LocalDateTime ts = LocalDateTime.of(2025, 1, 15, 10, 30);
        TelemetriaEntity entity = new TelemetriaEntity("endpoint", 120L, ts);

        assertThat(entity.getEndpoint()).isEqualTo("endpoint");
        assertThat(entity.getTempoRespostaMs()).isEqualTo(120L);
        assertThat(entity.getTimestamp()).isEqualTo(ts);

        entity.setId(99L);
        entity.setEndpoint("outro");
        entity.setTempoRespostaMs(250L);
        LocalDateTime novoTs = ts.plusMinutes(5);
        entity.setTimestamp(novoTs);

        assertThat(entity.getId()).isEqualTo(99L);
        assertThat(entity.getEndpoint()).isEqualTo("outro");
        assertThat(entity.getTempoRespostaMs()).isEqualTo(250L);
        assertThat(entity.getTimestamp()).isEqualTo(novoTs);
    }
}
