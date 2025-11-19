package br.gov.caixa.painelinvestimentos.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SimularInvestimentoRequestDTO {

    @NotNull
    @Positive(message = "O campo clienteId deve ser um numero positivo.")
    private Long clienteId;

    @NotNull
    @Positive
    private Double valor;

    @NotNull
    @Min(1)
    private Integer prazoMeses;

    @NotBlank
    private String tipoProduto;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public Integer getPrazoMeses() { return prazoMeses; }
    public void setPrazoMeses(Integer prazoMeses) { this.prazoMeses = prazoMeses; }

    public String getTipoProduto() { return tipoProduto; }
    public void setTipoProduto(String tipoProduto) { this.tipoProduto = tipoProduto; }
}
