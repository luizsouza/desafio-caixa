package br.gov.caixa.painelinvestimentos.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "produtos")
public class ProdutoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Double rentabilidade;

    @Column(nullable = false)
    private String risco;

    @Column(nullable = false)
    private Double minValor;

    @Column(nullable = false)
    private Double maxValor;

    @Column(nullable = false)
    private Integer minPrazo;

    @Column(nullable = false)
    private Integer maxPrazo;

    public ProdutoEntity() {}

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getRentabilidade() { return rentabilidade; }
    public void setRentabilidade(Double rentabilidade) { this.rentabilidade = rentabilidade; }

    public String getRisco() { return risco; }
    public void setRisco(String risco) { this.risco = risco; }

    public Double getMinValor() { return minValor; }
    public void setMinValor(Double minValor) { this.minValor = minValor; }

    public Double getMaxValor() { return maxValor; }
    public void setMaxValor(Double maxValor) { this.maxValor = maxValor; }

    public Integer getMinPrazo() { return minPrazo; }
    public void setMinPrazo(Integer minPrazo) { this.minPrazo = minPrazo; }

    public Integer getMaxPrazo() { return maxPrazo; }
    public void setMaxPrazo(Integer maxPrazo) { this.maxPrazo = maxPrazo; }
}