package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ProdutoServiceTest {

    @Test
    void deveListarTodosOsProdutosComSucesso() {
        ProdutoRepository repository = Mockito.mock(ProdutoRepository.class);

        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(1L);
        entity.setNome("CDB Teste");
        entity.setTipo("CDB");
        entity.setRisco("BAIXO");
        entity.setRentabilidade(1.0);
        entity.setMinValor(1000.0);
        entity.setMaxValor(5000.0);
        entity.setMinPrazo(6);
        entity.setMaxPrazo(12);

        when(repository.findAll()).thenReturn(List.of(entity));

        ProdutoService service = new ProdutoService(repository);

        List<ProdutoDTO> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("CDB Teste", resultado.get(0).getNome());
        assertEquals("BAIXO", resultado.get(0).getRisco());
    }
}
