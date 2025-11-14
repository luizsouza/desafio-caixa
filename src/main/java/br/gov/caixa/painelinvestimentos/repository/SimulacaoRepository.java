package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulacaoRepository extends JpaRepository<SimulacaoEntity, Long> {
}
