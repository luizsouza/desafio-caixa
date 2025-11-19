package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve listar produtos convertendo todos os campos importantes")
    void shouldListProducts() {
        ProdutoEntity entity = criarProduto(1L, "CDB Caixa", "CDB");
        when(produtoRepository.findAll()).thenReturn(List.of(entity));

        List<ProdutoDTO> produtos = produtoService.listarTodos();

        assertThat(produtos).hasSize(1);
        ProdutoDTO dto = produtos.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("CDB Caixa");
        assertThat(dto.getTipo()).isEqualTo("CDB");
    }

    @Test
    @DisplayName("Deve buscar produto por id e lançar exceção quando não existir")
    void shouldHandleProductById() {
        ProdutoEntity entity = criarProduto(2L, "Fundo XPTO", "FUNDO");
        when(produtoRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        ProdutoDTO dto = produtoService.buscarPorId(2L);
        assertThat(dto.getNome()).isEqualTo("Fundo XPTO");

        assertThatThrownBy(() -> produtoService.buscarPorId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    private ProdutoEntity criarProduto(Long id, String nome, String tipo) {
        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setTipo(tipo);
        entity.setRentabilidade(0.12);
        entity.setRisco("BAIXO");
        return entity;
    }
}
