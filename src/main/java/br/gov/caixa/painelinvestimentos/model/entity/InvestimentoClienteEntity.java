package br.gov.caixa.painelinvestimentos.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "investimentos_cliente")
public class InvestimentoClienteEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;

    private String tipo;

    private Double valor;

    private Double rentabilidade;

    private LocalDate data;

    public InvestimentoClienteEntity() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Double getRentabilidade() { return rentabilidade; }
    public void setRentabilidade(Double rentabilidade) { this.rentabilidade = rentabilidade; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
}
