package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProdutoRepositoryDataTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve carregar os produtos seeds do SQLite e consultar por tipo")
    void shouldLoadSeedProductsAndFindByTipo() {
        List<ProdutoEntity> todos = produtoRepository.findAll();
        assertThat(todos).isNotEmpty();
        assertThat(todos)
                .extracting(ProdutoEntity::getNome)
                .contains("CDB Caixa 2026", "Fundo XPTO", "Ações Arrojadas");

        assertThat(produtoRepository.findByTipoIgnoreCase("cdb"))
                .isPresent()
                .get()
                .extracting(ProdutoEntity::getRisco)
                .isEqualTo("BAIXO");
    }
}
