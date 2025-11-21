package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendacaoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private RecomendacaoService recomendacaoService;

    @Test
    @DisplayName("Deve recomendar apenas produtos compatíveis com perfil conservador")
    void shouldFilterConservativeProducts() {
        when(produtoRepository.findAll()).thenReturn(criarProdutos());

        List<ProdutoRecomendadoDTO> recomendados = recomendacaoService.recomendarPorPerfil("conservador");

        assertThat(recomendados).hasSize(1);
        assertThat(recomendados.get(0).getNome()).isEqualTo("CDB Conservador");
    }

    @Test
    @DisplayName("Perfis agressivos devem ordenar produtos pela maior pontuação")
    void shouldSortForAggressiveProfile() {
        when(produtoRepository.findAll()).thenReturn(criarProdutos());

        List<ProdutoRecomendadoDTO> recomendados = recomendacaoService.recomendarPorPerfil("agressivo");

        assertThat(recomendados).hasSize(3);
        assertThat(recomendados.get(0).getNome()).isEqualTo("Ações Arrojadas");
        assertThat(recomendados.get(2).getNome()).isEqualTo("CDB Conservador");
    }

    @Test
    @DisplayName("Perfil moderado deve trazer BAIXO e MÉDIO e pontuar igual à rentabilidade")
    void shouldFilterAndScoreModerateProfile() {
        when(produtoRepository.findAll()).thenReturn(criarProdutos());

        List<ProdutoRecomendadoDTO> recomendados = recomendacaoService.recomendarPorPerfil("moderado");

        assertThat(recomendados).hasSize(2);
        assertThat(recomendados)
                .extracting(ProdutoRecomendadoDTO::getNome)
                .containsExactly("Fundo Moderado", "CDB Conservador"); // ordenado por pontuação 1.2 > 0.8
        assertThat(recomendados.get(0).getPontuacao()).isEqualTo(1.2);
        assertThat(recomendados.get(1).getPontuacao()).isEqualTo(0.8);
    }

    @Test
    @DisplayName("Perfis inválidos devem disparar IllegalArgumentException")
    void shouldRejectInvalidProfile() {
        assertThatThrownBy(() -> recomendacaoService.recomendarPorPerfil("desconhecido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Perfil");
    }

    private List<ProdutoEntity> criarProdutos() {
        ProdutoEntity conservador = new ProdutoEntity();
        conservador.setId(1L);
        conservador.setNome("CDB Conservador");
        conservador.setTipo("CDB");
        conservador.setRisco("BAIXO");
        conservador.setRentabilidade(0.8);

        ProdutoEntity moderado = new ProdutoEntity();
        moderado.setId(2L);
        moderado.setNome("Fundo Moderado");
        moderado.setTipo("FUNDO");
        moderado.setRisco("MÉDIO");
        moderado.setRentabilidade(1.2);

        ProdutoEntity agressivo = new ProdutoEntity();
        agressivo.setId(3L);
        agressivo.setNome("Ações Arrojadas");
        agressivo.setTipo("AÇÕES");
        agressivo.setRisco("ALTO");
        agressivo.setRentabilidade(2.0);

        return List.of(conservador, moderado, agressivo);
    }
}
