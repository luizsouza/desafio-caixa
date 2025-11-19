package br.gov.caixa.painelinvestimentos.model.entity;

import br.gov.caixa.painelinvestimentos.model.converter.LocalDateTimeConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetria")
public class TelemetriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "tempo_resposta_ms", nullable = false)
    private Long tempoRespostaMs;

    @Column(nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime timestamp;

    public TelemetriaEntity() {}

    public TelemetriaEntity(String endpoint, Long tempoRespostaMs, LocalDateTime timestamp) {
        this.endpoint = endpoint;
        this.tempoRespostaMs = tempoRespostaMs;
        this.timestamp = timestamp;
    }

    // GETTERS & SETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Long getTempoRespostaMs() {
        return tempoRespostaMs;
    }
    public void setTempoRespostaMs(Long tempoRespostaMs) {
        this.tempoRespostaMs = tempoRespostaMs;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
