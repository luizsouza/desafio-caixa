package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    // Busca um produto pelo tipo, ignorando maiúsculas/minúsculas
    Optional<ProdutoEntity> findByTipoIgnoreCase(String tipo);
}
