package br.gov.caixa.painelinvestimentos.model.dto;

public class ProdutoDTO {

    private Long id;
    private String nome;
    private String tipo;
    private String risco;
    private Double rentabilidade;
    private Double minValor;
    private Double maxValor;
    private Integer minPrazo;
    private Integer maxPrazo;

    public ProdutoDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getRisco() { return risco; }
    public void setRisco(String risco) { this.risco = risco; }

    public Double getRentabilidade() { return rentabilidade; }
    public void setRentabilidade(Double rentabilidade) { this.rentabilidade = rentabilidade; }

    public Double getMinValor() { return minValor; }
    public void setMinValor(Double minValor) { this.minValor = minValor; }

    public Double getMaxValor() { return maxValor; }
    public void setMaxValor(Double maxValor) { this.maxValor = maxValor; }

    public Integer getMinPrazo() { return minPrazo; }
    public void setMinPrazo(Integer minPrazo) { this.minPrazo = minPrazo; }

    public Integer getMaxPrazo() { return maxPrazo; }
    public void setMaxPrazo(Integer maxPrazo) { this.maxPrazo = maxPrazo; }
}