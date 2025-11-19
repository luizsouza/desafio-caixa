package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestimentoRepository extends JpaRepository<InvestimentoClienteEntity, Long> {

    List<InvestimentoClienteEntity> findByClienteIdOrderByDataDesc(Long clienteId);
}
