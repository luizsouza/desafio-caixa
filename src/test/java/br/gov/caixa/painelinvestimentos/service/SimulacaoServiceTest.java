package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SimulacaoServiceTest {

    @Test
    void deveSimularInvestimentoComSucesso() {
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);
        SimulacaoRepository simulacaoRepository = mock(SimulacaoRepository.class);

        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("CDB");
        produto.setRentabilidade(1.0);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(simulacaoRepository.save(any(SimulacaoEntity.class)))
                .thenAnswer(inv -> {
                    SimulacaoEntity s = inv.getArgument(0);
                    s.setId(10L);
                    return s;
                });

        SimulacaoService service = new SimulacaoService(produtoRepository, simulacaoRepository);

        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        request.setClienteId(1L);
        request.setProdutoId(1L);
        request.setValorInvestido(1000.0);
        request.setPrazoMeses(12);

        SimulacaoResponseDTO response = service.simular(request);

        assertNotNull(response);
        assertEquals(1L, response.getClienteId());
        assertEquals(10L, response.getSimulacaoId());
        assertEquals(1000.0, response.getValorInvestido());
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);
        SimulacaoRepository simulacaoRepository = mock(SimulacaoRepository.class);

        when(produtoRepository.findById(99L))
                .thenReturn(Optional.empty());

        SimulacaoService service = new SimulacaoService(produtoRepository, simulacaoRepository);

        SimulacaoRequestDTO request = new SimulacaoRequestDTO();
        request.setClienteId(1L);
        request.setProdutoId(99L);
        request.setValorInvestido(1000.0);
        request.setPrazoMeses(12);

        assertThrows(IllegalArgumentException.class, () ->
                service.simular(request));
    }
}