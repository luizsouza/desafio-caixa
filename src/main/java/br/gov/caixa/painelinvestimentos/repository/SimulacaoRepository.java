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
     * Usado para telemetria diária (00:00 ate 23:59).
     */
    List<SimulacaoEntity> findByDataSimulacaoBetween(LocalDateTime inicio, LocalDateTime fim);
}

