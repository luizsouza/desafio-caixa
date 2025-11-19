package br.gov.caixa.painelinvestimentos.model.dto;

import java.time.LocalDate;

public class InvestimentoHistoricoDTO {

    private Long id;
    private String tipo;
    private Double valor;
    private Double rentabilidade;
    private LocalDate data;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Double getRentabilidade() { return rentabilidade; }
    public void setRentabilidade(Double rentabilidade) { this.rentabilidade = rentabilidade; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
