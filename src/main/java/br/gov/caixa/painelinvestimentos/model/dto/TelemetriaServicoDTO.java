package br.gov.caixa.painelinvestimentos.model.dto;

public class TelemetriaServicoDTO {

    private String nome;
    private Long quantidadeChamadas;
    private double mediaTempoRespostaMs;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getQuantidadeChamadas() {
        return quantidadeChamadas;
    }
    public void setQuantidadeChamadas(Long quantidadeChamadas) {
        this.quantidadeChamadas = quantidadeChamadas;
    }

    public double getMediaTempoRespostaMs() {
        return mediaTempoRespostaMs;
    }
    public void setMediaTempoRespostaMs(double mediaTempoRespostaMs) {
        this.mediaTempoRespostaMs = mediaTempoRespostaMs;
    }
}
