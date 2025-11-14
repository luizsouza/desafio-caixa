package br.gov.caixa.painelinvestimentos.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetria")
public class TelemetriaEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String servico;

    private Long tempoRespostaMs;

    private LocalDateTime dataChamada;

    public TelemetriaEntity() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public Long getTempoRespostaMs() { return tempoRespostaMs; }
    public void setTempoRespostaMs(Long tempoRespostaMs) { this.tempoRespostaMs = tempoRespostaMs; }

    public LocalDateTime getDataChamada() { return dataChamada; }
    public void setDataChamada(LocalDateTime dataChamada) { this.dataChamada = dataChamada; }
}
