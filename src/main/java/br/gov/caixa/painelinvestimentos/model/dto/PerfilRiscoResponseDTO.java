package br.gov.caixa.painelinvestimentos.model.dto;

import br.gov.caixa.painelinvestimentos.model.PerfilRisco;

public class PerfilRiscoResponseDTO {

    private Long clienteId;
    private PerfilRisco perfil;
    private int pontuacaoTotal;
    private int pontuacaoVolume;
    private int pontuacaoFrequencia;
    private int pontuacaoLiquidez;
    private String descricao;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public PerfilRisco getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilRisco perfil) {
        this.perfil = perfil;
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    public void setPontuacaoTotal(int pontuacaoTotal) {
        this.pontuacaoTotal = pontuacaoTotal;
    }

    public int getPontuacaoVolume() {
        return pontuacaoVolume;
    }

    public void setPontuacaoVolume(int pontuacaoVolume) {
        this.pontuacaoVolume = pontuacaoVolume;
    }

    public int getPontuacaoFrequencia() {
        return pontuacaoFrequencia;
    }

    public void setPontuacaoFrequencia(int pontuacaoFrequencia) {
        this.pontuacaoFrequencia = pontuacaoFrequencia;
    }

    public int getPontuacaoLiquidez() {
        return pontuacaoLiquidez;
    }

    public void setPontuacaoLiquidez(int pontuacaoLiquidez) {
        this.pontuacaoLiquidez = pontuacaoLiquidez;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
