package br.gov.caixa.painelinvestimentos.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


public class SimulacaoRequestDTO {

    @NotNull
    @Min(1)
    @Schema(example = "1", description = "Identificador do cliente")
    private Long clienteId;

    @NotNull
    @Min(1)
    @Schema(example = "1", description = "Identificador do produto de investimento")
    private Long produtoId;

    @NotNull
    @Min(1)
    @Schema(example = "1000.0", description = "Valor a ser investido em reais")
    private Double valorInvestido;

    @NotNull
    @Min(1)
    @Schema(example = "12", description = "Prazo da aplicação em meses")
    private Integer prazoMeses;

    public SimulacaoRequestDTO() {
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Double getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(Double valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }
}