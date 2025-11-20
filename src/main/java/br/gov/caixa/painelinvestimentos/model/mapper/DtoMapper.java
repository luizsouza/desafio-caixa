package br.gov.caixa.painelinvestimentos.model.mapper;

import br.gov.caixa.painelinvestimentos.model.dto.*;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.TelemetriaEntity;

import java.util.List;

/**
 * Mapper para converter entidades em DTOs, evitando repetição
 * de código nos serviços.
 */
public final class DtoMapper {

    private DtoMapper() {}

    public static ProdutoDTO toProdutoDTO(ProdutoEntity entity) {
        if (entity == null) return null;
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTipo(entity.getTipo());
        dto.setRentabilidade(entity.getRentabilidade());
        dto.setRisco(entity.getRisco());
        return dto;
    }

    public static InvestimentoHistoricoDTO toInvestimentoHistoricoDTO(InvestimentoClienteEntity entity) {
        if (entity == null) return null;
        InvestimentoHistoricoDTO dto = new InvestimentoHistoricoDTO();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setValor(entity.getValor());
        dto.setRentabilidade(entity.getRentabilidade());
        dto.setData(entity.getData());
        return dto;
    }

    public static SimulacaoHistoricoDTO toSimulacaoHistoricoDTO(SimulacaoEntity simulacao) {
        if (simulacao == null) return null;
        SimulacaoHistoricoDTO dto = new SimulacaoHistoricoDTO();
        dto.setId(simulacao.getId());
        dto.setClienteId(simulacao.getClienteId());
        dto.setProduto(simulacao.getProduto().getNome());
        dto.setValorInvestido(simulacao.getValorInvestido());
        dto.setValorFinal(simulacao.getValorFinal());
        dto.setPrazoMeses(simulacao.getPrazoMeses());
        dto.setDataSimulacao(simulacao.getDataSimulacao());
        return dto;
    }

    public static TelemetriaServicoDTO toTelemetriaServicoDTO(String endpoint, List<TelemetriaEntity> registros) {
        TelemetriaServicoDTO dto = new TelemetriaServicoDTO();
        dto.setNome(endpoint);
        dto.setQuantidadeChamadas((long) registros.size());
        dto.setMediaTempoRespostaMs(
                Math.round(
                        registros.stream()
                                .mapToLong(TelemetriaEntity::getTempoRespostaMs)
                                .average()
                                .orElse(0)
                )
        );
        return dto;
    }

    public static ProdutoRecomendadoDTO toProdutoRecomendadoDTO(ProdutoEntity produto, double pontuacao) {
        if (produto == null) return null;
        ProdutoRecomendadoDTO dto = new ProdutoRecomendadoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setTipo(produto.getTipo());
        dto.setRisco(produto.getRisco());
        dto.setRentabilidade(produto.getRentabilidade());
        dto.setPontuacao(pontuacao);
        return dto;
    }
}
