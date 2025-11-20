package br.gov.caixa.painelinvestimentos.model.mapper;

import br.gov.caixa.painelinvestimentos.model.dto.*;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.TelemetriaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    @Test
    @DisplayName("Deve mapear ProdutoEntity para ProdutoDTO")
    void shouldMapProdutoEntity() {
        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(10L);
        entity.setNome("CDB");
        entity.setTipo("POS");
        entity.setRentabilidade(0.11);
        entity.setRisco("BAIXO");

        ProdutoDTO dto = DtoMapper.toProdutoDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getNome()).isEqualTo("CDB");
        assertThat(dto.getTipo()).isEqualTo("POS");
        assertThat(dto.getRentabilidade()).isEqualTo(0.11);
        assertThat(dto.getRisco()).isEqualTo("BAIXO");
    }

    @Test
    @DisplayName("Deve mapear InvestimentoClienteEntity para InvestimentoHistoricoDTO")
    void shouldMapInvestimentoHistorico() {
        InvestimentoClienteEntity entity = new InvestimentoClienteEntity();
        entity.setId(7L);
        entity.setClienteId(5L);
        entity.setTipo("LCI");
        entity.setValor(2500.0);
        entity.setRentabilidade(0.07);
        entity.setData(LocalDate.of(2024, 5, 2));

        InvestimentoHistoricoDTO dto = DtoMapper.toInvestimentoHistoricoDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getTipo()).isEqualTo("LCI");
        assertThat(dto.getValor()).isEqualTo(2500.0);
        assertThat(dto.getRentabilidade()).isEqualTo(0.07);
        assertThat(dto.getData()).isEqualTo(LocalDate.of(2024, 5, 2));
    }

    @Test
    @DisplayName("Deve mapear SimulacaoEntity para SimulacaoHistoricoDTO")
    void shouldMapSimulacaoHistorico() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Tesouro");

        SimulacaoEntity simulacao = new SimulacaoEntity();
        simulacao.setId(3L);
        simulacao.setClienteId(99L);
        simulacao.setProduto(produto);
        simulacao.setValorInvestido(1000.0);
        simulacao.setValorFinal(1200.0);
        simulacao.setPrazoMeses(12);
        simulacao.setDataSimulacao(LocalDateTime.of(2024, 10, 5, 9, 30));

        SimulacaoHistoricoDTO dto = DtoMapper.toSimulacaoHistoricoDTO(simulacao);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getClienteId()).isEqualTo(99L);
        assertThat(dto.getProduto()).isEqualTo("Tesouro");
        assertThat(dto.getValorInvestido()).isEqualTo(1000.0);
        assertThat(dto.getValorFinal()).isEqualTo(1200.0);
        assertThat(dto.getPrazoMeses()).isEqualTo(12);
        assertThat(dto.getDataSimulacao()).isEqualTo(LocalDateTime.of(2024, 10, 5, 9, 30));
    }

    @Test
    @DisplayName("Deve mapear lista de TelemetriaEntity para TelemetriaServicoDTO calculando média")
    void shouldMapTelemetriaServico() {
        TelemetriaEntity primeira = new TelemetriaEntity("GET /telemetria", 100L, LocalDateTime.now().minusMinutes(1));
        TelemetriaEntity segunda = new TelemetriaEntity("GET /telemetria", 300L, LocalDateTime.now());

        TelemetriaServicoDTO dto = DtoMapper.toTelemetriaServicoDTO("GET /telemetria", List.of(primeira, segunda));

        assertThat(dto).isNotNull();
        assertThat(dto.getNome()).isEqualTo("GET /telemetria");
        assertThat(dto.getQuantidadeChamadas()).isEqualTo(2L);
        assertThat(dto.getMediaTempoRespostaMs()).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Deve retornar null quando entidades forem null")
    void shouldReturnNullForNullEntities() {
        assertThat(DtoMapper.toProdutoDTO(null)).isNull();
        assertThat(DtoMapper.toInvestimentoHistoricoDTO(null)).isNull();
        assertThat(DtoMapper.toSimulacaoHistoricoDTO(null)).isNull();
    }
}
