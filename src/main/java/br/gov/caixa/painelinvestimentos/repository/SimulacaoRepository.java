package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SimulacaoRepository extends JpaRepository<SimulacaoEntity, Long> {

    // Busca todas as simulações de um cliente específico
    List<SimulacaoEntity> findByClienteId(Long clienteId);
}