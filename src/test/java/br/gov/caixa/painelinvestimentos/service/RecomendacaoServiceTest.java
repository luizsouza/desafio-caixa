package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecomendacaoServiceTest {

    @Test
    void deveRecomendarSomenteProdutosCompatíveisComPerfilConservador() {
        // mocks
        PerfilRiscoService perfilRiscoService = mock(PerfilRiscoService.class);
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);

        // perfil calculado como CONSERVADOR
        PerfilRiscoResponseDTO perfil = new PerfilRiscoResponseDTO();
        perfil.setClienteId(1L);
        perfil.setPerfil(PerfilRisco.CONSERVADOR);
        perfil.setPontuacaoTotal(10);

        when(perfilRiscoService.calcularPerfil(1L)).thenReturn(perfil);

        // produtos disponíveis
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
        moderado.setRisco("MEDIO");
        moderado.setRentabilidade(1.2);

        ProdutoEntity arrojado = new ProdutoEntity();
        arrojado.setId(3L);
        arrojado.setNome("Ações Arrojadas");
        arrojado.setTipo("ACOES");
        arrojado.setRisco("ALTO");
        arrojado.setRentabilidade(2.0);

        when(produtoRepository.findAll())
                .thenReturn(List.of(conservador, moderado, arrojado));

        RecomendacaoService service = new RecomendacaoService(perfilRiscoService, produtoRepository);

        // act
        List<ProdutoRecomendadoDTO> recomendados = service.recomendar(1L);

        // assert
        assertNotNull(recomendados);
        assertEquals(1, recomendados.size(), "Cliente conservador deve receber apenas produtos de risco BAIXO");

        ProdutoRecomendadoDTO r = recomendados.get(0);
        assertEquals(1L, r.getId());
        assertEquals("CDB Conservador", r.getNome());
        assertEquals("BAIXO", r.getRisco());
    }

    @Test
    void deveOrdenarProdutosPorPontuacaoParaPerfilAgressivo() {
        // mocks
        PerfilRiscoService perfilRiscoService = mock(PerfilRiscoService.class);
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);

        PerfilRiscoResponseDTO perfil = new PerfilRiscoResponseDTO();
        perfil.setClienteId(2L);
        perfil.setPerfil(PerfilRisco.AGRESSIVO);
        perfil.setPontuacaoTotal(80);

        when(perfilRiscoService.calcularPerfil(2L)).thenReturn(perfil);

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
        moderado.setRisco("MEDIO");
        moderado.setRentabilidade(1.2);

        ProdutoEntity arrojado = new ProdutoEntity();
        arrojado.setId(3L);
        arrojado.setNome("Ações Arrojadas");
        arrojado.setTipo("ACOES");
        arrojado.setRisco("ALTO");
        arrojado.setRentabilidade(2.0);

        when(produtoRepository.findAll())
                .thenReturn(List.of(conservador, moderado, arrojado));

        RecomendacaoService service = new RecomendacaoService(perfilRiscoService, produtoRepository);

        // act
        List<ProdutoRecomendadoDTO> recomendados = service.recomendar(2L);

        // assert
        assertEquals(3, recomendados.size(), "Agressivo pode receber todos os produtos");

        // ordenação: maior pontuação primeiro -> maior rentabilidade
        assertEquals("Ações Arrojadas", recomendados.get(0).getNome());
        assertEquals("Fundo Moderado", recomendados.get(1).getNome());
        assertEquals("CDB Conservador", recomendados.get(2).getNome());

        // sanity check das pontuações (não precisa ser exato, mas crescente/decrescente)
        assertTrue(recomendados.get(0).getPontuacao() >= recomendados.get(1).getPontuacao());
        assertTrue(recomendados.get(1).getPontuacao() >= recomendados.get(2).getPontuacao());
    }
}