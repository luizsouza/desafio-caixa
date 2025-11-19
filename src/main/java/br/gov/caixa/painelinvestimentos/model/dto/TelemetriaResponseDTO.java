package br.gov.caixa.painelinvestimentos.model.dto;

import java.util.List;

public class TelemetriaResponseDTO {

    private TelemetriaPeriodoDTO periodo;
    private List<TelemetriaServicoDTO> servicos;

    public TelemetriaPeriodoDTO getPeriodo() {
        return periodo;
    }
    public void setPeriodo(TelemetriaPeriodoDTO periodo) {
        this.periodo = periodo;
    }

    public List<TelemetriaServicoDTO> getServicos() {
        return servicos;
    }
    public void setServicos(List<TelemetriaServicoDTO> servicos) {
        this.servicos = servicos;
    }
}
