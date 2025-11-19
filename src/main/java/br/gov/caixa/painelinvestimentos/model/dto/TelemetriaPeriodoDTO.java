package br.gov.caixa.painelinvestimentos.model.dto;

public class TelemetriaPeriodoDTO {

    private String inicio;
    private String fim;

    public String getInicio() {
        return inicio;
    }
    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }
    public void setFim(String fim) {
        this.fim = fim;
    }
}
