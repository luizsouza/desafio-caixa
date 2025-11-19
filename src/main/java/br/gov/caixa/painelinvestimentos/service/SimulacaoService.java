package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.*;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.InvestimentoRepository;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final InvestimentoRepository investimentoRepository;

    public SimulacaoService(ProdutoRepository produtoRepository,
                            SimulacaoRepository simulacaoRepository,
                            InvestimentoRepository investimentoRepository) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.investimentoRepository = investimentoRepository;
    }

    @Transactional
    public SimularInvestimentoResponseDTO simularInvestimento(SimularInvestimentoRequestDTO request) {
        String tipoProduto = request.getTipoProduto().trim();
        ProdutoEntity produto = produtoRepository.findByTipoIgnoreCase(tipoProduto)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de produto não encontrado: " + tipoProduto));

        double valorFinal = calcularValorFinal(request.getValor(), produto.getRentabilidade(), request.getPrazoMeses());
        double rentabilidadeEfetiva = calcularRentabilidadeEfetiva(produto.getRentabilidade(), request.getPrazoMeses());
        LocalDateTime agora = LocalDateTime.now();

        SimulacaoEntity simulacao = new SimulacaoEntity();
        simulacao.setClienteId(request.getClienteId());
        simulacao.setProduto(produto);
        simulacao.setValorInvestido(request.getValor());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(request.getPrazoMeses());
        simulacao.setDataSimulacao(agora);
        simulacaoRepository.save(simulacao);
        registrarInvestimentoHistorico(request, produto, agora);

        ProdutoValidadoDTO produtoValidado = montarProdutoValidado(produto);
        ResultadoSimulacaoDTO resultado = new ResultadoSimulacaoDTO();
        resultado.setValorFinal(valorFinal);
        resultado.setRentabilidadeEfetiva(rentabilidadeEfetiva);
        resultado.setPrazoMeses(request.getPrazoMeses());

        SimularInvestimentoResponseDTO response = new SimularInvestimentoResponseDTO();
        response.setProdutoValidado(produtoValidado);
        response.setResultadoSimulacao(resultado);
        response.setDataSimulacao(agora.atZone(ZoneId.systemDefault()).toInstant());
        return response;
    }

    @Transactional(readOnly = true)
    public List<SimulacaoHistoricoDTO> listarHistorico() {
        return simulacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataSimulacao"))
                .stream()
                .map(this::toHistoricoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimulacoesPorProdutoDiaDTO> buscarSimulacoesPorProdutoNoDia(LocalDate dia) {
        LocalDate dataConsulta = dia != null ? dia : LocalDate.now();
        LocalDateTime inicio = dataConsulta.atStartOfDay();
        LocalDateTime fim = dataConsulta.atTime(LocalTime.MAX);

        List<SimulacaoEntity> simulacoes = simulacaoRepository.findByDataSimulacaoBetween(inicio, fim);

        Map<ProdutoEntity, List<SimulacaoEntity>> agrupado = simulacoes.stream()
                .collect(Collectors.groupingBy(SimulacaoEntity::getProduto));

        List<SimulacoesPorProdutoDiaDTO> resposta = new ArrayList<>();
        for (Map.Entry<ProdutoEntity, List<SimulacaoEntity>> entry : agrupado.entrySet()) {
            ProdutoEntity produto = entry.getKey();
            List<SimulacaoEntity> lista = entry.getValue();

            SimulacoesPorProdutoDiaDTO dto = new SimulacoesPorProdutoDiaDTO();
            dto.setProduto(produto.getNome());
            dto.setData(dataConsulta.toString());
            dto.setQuantidadeSimulacoes(lista.size());
            dto.setMediaValorFinal(
                    lista.stream()
                            .mapToDouble(SimulacaoEntity::getValorInvestido)
                            .average()
                            .orElse(0.0)
            );
            resposta.add(dto);
        }

        resposta.sort(Comparator.comparing(SimulacoesPorProdutoDiaDTO::getQuantidadeSimulacoes).reversed());
        return resposta;
    }

    private SimulacaoHistoricoDTO toHistoricoDTO(SimulacaoEntity simulacao) {
        SimulacaoHistoricoDTO dto = new SimulacaoHistoricoDTO();
        dto.setId(simulacao.getId());
        dto.setClienteId(simulacao.getClienteId());
        dto.setProduto(simulacao.getProduto().getNome());
        dto.setValorInvestido(simulacao.getValorInvestido());
        dto.setValorFinal(simulacao.getValorFinal());
        dto.setPrazoMeses(simulacao.getPrazoMeses());
        dto.setDataSimulacao(simulacao.getDataSimulacao());
        return dto;
    }

    private void registrarInvestimentoHistorico(SimularInvestimentoRequestDTO request,
                                                ProdutoEntity produto,
                                                LocalDateTime dataSimulacao) {
        InvestimentoClienteEntity investimento = new InvestimentoClienteEntity();
        investimento.setClienteId(request.getClienteId());
        investimento.setTipo(produto.getTipo());
        investimento.setValor(request.getValor());
        investimento.setRentabilidade(produto.getRentabilidade());
        investimento.setData(dataSimulacao.toLocalDate());
        investimentoRepository.save(investimento);
    }

    /**
     * Fórmula baseada no enunciado: rentabilidade anual simples.
     */
    private double calcularValorFinal(double valorInvestido, double rentabilidadeAnual, int prazoMeses) {
        double prazoAnos = prazoMeses / 12d;
        return valorInvestido * (1 + rentabilidadeAnual * prazoAnos);
    }

    private double calcularRentabilidadeEfetiva(double rentabilidadeAnual, int prazoMeses) {
        double prazoAnos = prazoMeses / 12d;
        return rentabilidadeAnual * prazoAnos;
    }

    private ProdutoValidadoDTO montarProdutoValidado(ProdutoEntity produto) {
        ProdutoValidadoDTO dto = new ProdutoValidadoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setTipo(produto.getTipo());
        dto.setRentabilidade(produto.getRentabilidade());
        dto.setRisco(produto.getRisco());
        return dto;
    }
}
