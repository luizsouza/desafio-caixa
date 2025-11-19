package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SimulacaoRepository extends JpaRepository<SimulacaoEntity, Long> {

    /**
     * Busca todas as simulações realizadas por um cliente específico.
     */
    List<SimulacaoEntity> findByClienteId(Long clienteId);

    /**
     * Busca as simulações realizadas dentro de um intervalo de data/hora.
     * Usado para telemetria diária (00:00 até 23:59).
     */
    List<SimulacaoEntity> findByDataSimulacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Busca todas as simulações de um produto em um intervalo de tempo.
     * Suporte direto ao endpoint /simulacoes/por-produto-dia.
     */
    List<SimulacaoEntity> findByProdutoIdAndDataSimulacaoBetween(Long produtoId,
                                                                 LocalDateTime inicio,
                                                                 LocalDateTime fim);
}
