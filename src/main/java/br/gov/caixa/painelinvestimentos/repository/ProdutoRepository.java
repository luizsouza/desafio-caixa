package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
}
