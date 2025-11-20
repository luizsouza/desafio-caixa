package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacoesPorProdutoDiaDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.InvestimentoRepository;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulacaoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private SimulacaoRepository simulacaoRepository;
    @Mock
    private InvestimentoRepository investimentoRepository;
    @InjectMocks
    private SimulacaoService simulacaoService;

    @Test
    @DisplayName("Deve realizar uma simulação válida, persistir o resultado e registrar o histórico do cliente")
    void shouldSimulateInvestment() {
        ProdutoEntity produto = produto();
        when(produtoRepository.findByTipoIgnoreCase("CDB"))
                .thenReturn(Optional.of(produto));

        SimularInvestimentoRequestDTO request = new SimularInvestimentoRequestDTO();
        request.setClienteId(10L);
        request.setValor(5000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("CDB");

        SimularInvestimentoResponseDTO response = simulacaoService.simularInvestimento(request);

        assertThat(response.getProdutoValidado().getNome()).isEqualTo("Produto Teste");
        assertThat(response.getResultadoSimulacao().getValorFinal()).isGreaterThan(5000);
        assertThat(response.getDataSimulacao()).isNotNull();

        ArgumentCaptor<SimulacaoEntity> captor = ArgumentCaptor.forClass(SimulacaoEntity.class);
        verify(simulacaoRepository).save(captor.capture());
        assertThat(captor.getValue().getClienteId()).isEqualTo(10L);
        assertThat(captor.getValue().getValorFinal()).isGreaterThan(5000);
        verify(investimentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve informar quando o tipo de produto não existe")
    void shouldRejectUnknownProductType() {
        when(produtoRepository.findByTipoIgnoreCase("desconhecido"))
                .thenReturn(Optional.empty());

        SimularInvestimentoRequestDTO request = new SimularInvestimentoRequestDTO();
        request.setClienteId(10L);
        request.setValor(5000.0);
        request.setPrazoMeses(12);
        request.setTipoProduto("desconhecido");

        assertThatThrownBy(() -> simulacaoService.simularInvestimento(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de produto");
    }

    @Test
    @DisplayName("Listar histórico deve manter dados essenciais")
    void shouldListHistory() {
        ProdutoEntity produto = produto();
        SimulacaoEntity entity = new SimulacaoEntity();
        entity.setId(1L);
        entity.setClienteId(5L);
        entity.setProduto(produto);
        entity.setValorInvestido(2000.0);
        entity.setValorFinal(2100.0);
        entity.setPrazoMeses(6);
        entity.setDataSimulacao(LocalDateTime.now());

        when(simulacaoRepository.findAll(any(Sort.class))).thenReturn(List.of(entity));

        List<SimulacaoHistoricoDTO> historico = simulacaoService.listarHistorico();

        assertThat(historico).hasSize(1);
        assertThat(historico.get(0).getProduto()).isEqualTo("Produto Teste");
    }

    @Test
    @DisplayName("Deve agrupar simulações por produto e dia usando valor final")
    void shouldGroupSimulationsByDay() {
        ProdutoEntity produto = produto();
        SimulacaoEntity a = new SimulacaoEntity();
        a.setProduto(produto);
        a.setValorFinal(2200.0);
        a.setDataSimulacao(LocalDateTime.of(2025, 10, 30, 10, 0));
        SimulacaoEntity b = new SimulacaoEntity();
        b.setProduto(produto);
        b.setValorFinal(4400.0);
        b.setDataSimulacao(LocalDateTime.of(2025, 10, 31, 12, 0));

        when(simulacaoRepository.findByDataSimulacaoBetween(any(), any()))
                .thenReturn(List.of(a, b));

        List<SimulacoesPorProdutoDiaDTO> resposta =
                simulacaoService.buscarSimulacoesPorProdutoPorDia(
                        LocalDate.parse("2025-10-01"),
                        LocalDate.parse("2025-10-31"));

        assertThat(resposta).hasSize(2);
        assertThat(resposta)
                .extracting(SimulacoesPorProdutoDiaDTO::getData)
                .containsExactly("2025-10-31", "2025-10-30");
        SimulacoesPorProdutoDiaDTO dtoMaisRecente = resposta.get(0);
        assertThat(dtoMaisRecente.getProduto()).isEqualTo("Produto Teste");
        assertThat(dtoMaisRecente.getQuantidadeSimulacoes()).isEqualTo(1);
        assertThat(dtoMaisRecente.getMediaValorFinal()).isEqualTo(4400.0);
    }

    @Test
    @DisplayName("Deve aplicar o período padrão de 30 dias quando início e fim são nulos")
    void shouldApplyDefaultPeriodWhenNullDates() {
        when(simulacaoRepository.findByDataSimulacaoBetween(any(), any())).thenReturn(List.of());

        simulacaoService.buscarSimulacoesPorProdutoPorDia(null, LocalDate.of(2025, 1, 31));

        var inicio = ArgumentCaptor.forClass(LocalDateTime.class);
        var fim = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(simulacaoRepository).findByDataSimulacaoBetween(inicio.capture(), fim.capture());

        assertThat(inicio.getValue()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0));
        assertThat(fim.getValue()).isEqualTo(LocalDateTime.of(2025, 1, 31, 23, 59, 59, 999_999_999));
    }

    @Test
    @DisplayName("Deve rejeitar período com data inicial depois da final")
    void shouldRejectInvalidDateRange() {
        assertThatThrownBy(() -> simulacaoService.buscarSimulacoesPorProdutoPorDia(
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 1, 31)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data inicial");
    }

    @Test
    @DisplayName("Não deve chamar repositório se houver parâmetro inválido")
    void shouldNotCallRepositoryOnInvalidRange() {
        assertThatThrownBy(() -> simulacaoService.buscarSimulacoesPorProdutoPorDia(
                LocalDate.parse("2025-02-01"), LocalDate.parse("2025-01-01"))).isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(simulacaoRepository);
    }

    private ProdutoEntity produto() {
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setTipo("CDB");
        produto.setRentabilidade(0.12);
        produto.setRisco("BAIXO");
        return produto;
    }
}