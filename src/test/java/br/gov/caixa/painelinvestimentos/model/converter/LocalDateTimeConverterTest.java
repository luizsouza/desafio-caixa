package br.gov.caixa.painelinvestimentos.model.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @Test
    @DisplayName("Converter deve serializar e desserializar LocalDateTime")
    void shouldConvertToAndFromDatabaseColumn() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 2, 3, 4, 5);

        String dbValue = converter.convertToDatabaseColumn(now);
        LocalDateTime restored = converter.convertToEntityAttribute(dbValue);

        assertThat(dbValue).isEqualTo("2025-01-02T03:04:05");
        assertThat(restored).isEqualTo(now);
    }

    @Test
    @DisplayName("Converter deve lidar com valores nulos")
    void shouldHandleNullValues() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
