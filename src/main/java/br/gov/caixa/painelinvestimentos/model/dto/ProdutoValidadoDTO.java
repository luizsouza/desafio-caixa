package br.gov.caixa.painelinvestimentos.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações do produto validado antes da simulação")
public class ProdutoValidadoDTO {

    @Schema(example = "101", description = "Identificador único do produto")
    private Long id;

    @Schema(example = "CDB Caixa 2026", description = "Nome comercial do produto")
    private String nome;

    @Schema(example = "CDB", description = "Tipo do produto de investimento")
    private String tipo;

    @Schema(example = "0.12", description = "Rentabilidade mensal do produto (em percentual)")
    private Double rentabilidade;

    @Schema(example = "Baixo", description = "Classificação de risco do produto")
    private String risco;

    // Getters e setters
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
}
