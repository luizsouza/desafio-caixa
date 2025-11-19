package br.gov.caixa.painelinvestimentos.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado final da simulação de investimento")
public class ResultadoSimulacaoDTO {

    @Schema(example = "11200.00", description = "Valor final calculado após o prazo da simulação")
    private Double valorFinal;

    @Schema(example = "0.12", description = "Rentabilidade efetiva total no período")
    private Double rentabilidadeEfetiva;

    @Schema(example = "12", description = "Prazo da simulação em meses")
    private Integer prazoMeses;

    public Double getValorFinal() {
        return valorFinal;
    }
    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Double getRentabilidadeEfetiva() {
        return rentabilidadeEfetiva;
    }
    public void setRentabilidadeEfetiva(Double rentabilidadeEfetiva) {
        this.rentabilidadeEfetiva = rentabilidadeEfetiva;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }
    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }
}
