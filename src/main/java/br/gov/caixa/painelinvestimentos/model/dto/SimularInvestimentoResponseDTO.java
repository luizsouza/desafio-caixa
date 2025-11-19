package br.gov.caixa.painelinvestimentos.model.dto;

import java.time.Instant;

public class SimularInvestimentoResponseDTO {

    private ProdutoValidadoDTO produtoValidado;
    private ResultadoSimulacaoDTO resultadoSimulacao;
    private Instant dataSimulacao;

    public ProdutoValidadoDTO getProdutoValidado() { return produtoValidado; }
    public void setProdutoValidado(ProdutoValidadoDTO produtoValidado) { this.produtoValidado = produtoValidado; }

    public ResultadoSimulacaoDTO getResultadoSimulacao() { return resultadoSimulacao; }
    public void setResultadoSimulacao(ResultadoSimulacaoDTO resultadoSimulacao) { this.resultadoSimulacao = resultadoSimulacao; }

    public Instant getDataSimulacao() { return dataSimulacao; }
    public void setDataSimulacao(Instant dataSimulacao) { this.dataSimulacao = dataSimulacao; }
}
