package br.gov.caixa.painelinvestimentos.model.dto;

public class SimulacoesPorProdutoDiaDTO {

    private String produto;
    private String data;
    private Integer quantidadeSimulacoes;
    private Double mediaValorFinal;

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Integer getQuantidadeSimulacoes() { return quantidadeSimulacoes; }
    public void setQuantidadeSimulacoes(Integer quantidadeSimulacoes) { this.quantidadeSimulacoes = quantidadeSimulacoes; }

    public Double getMediaValorFinal() { return mediaValorFinal; }
    public void setMediaValorFinal(Double mediaValorFinal) { this.mediaValorFinal = mediaValorFinal; }
}
