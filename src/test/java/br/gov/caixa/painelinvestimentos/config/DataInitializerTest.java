package br.gov.caixa.painelinvestimentos.config;

import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Test
    void devePopularBaseDeProdutosQuandoRepositorioEstaVazio() throws Exception {
        ProdutoRepository repository = mock(ProdutoRepository.class);

        when(repository.count()).thenReturn(0L);
        when(repository.save(any(ProdutoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        DataInitializer initializer = new DataInitializer();
        initializer.loadData(repository).run();

        verify(repository, times(3)).save(any(ProdutoEntity.class));
    }

    @Test
    void naoDevePopularBaseQuandoJaExisteProduto() throws Exception {
        ProdutoRepository repository = mock(ProdutoRepository.class);

        when(repository.count()).thenReturn(5L);

        DataInitializer initializer = new DataInitializer();
        initializer.loadData(repository).run();

        verify(repository, never()).save(any());
    }
}
