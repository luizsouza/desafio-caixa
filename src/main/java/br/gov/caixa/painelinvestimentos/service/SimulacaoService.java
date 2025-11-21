package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoValidadoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.ResultadoSimulacaoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimularInvestimentoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacoesPorProdutoDiaDTO;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.InvestimentoRepository;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    // Todas as operações de banco de dados (salvar simulação e histórico) ocorrem de forma atômica.

    @Transactional
    public SimularInvestimentoResponseDTO simularInvestimento(SimularInvestimentoRequestDTO request) {
        String tipoProduto = request.getTipoProduto().trim();
        ProdutoEntity produto = produtoRepository.findAll().stream()
                .filter(p -> p.getTipo() != null && p.getTipo().equalsIgnoreCase(tipoProduto))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de produto não encontrado: " + tipoProduto));

        double valorInvestido = arredondarDuasCasas(request.getValor());
        double valorFinal = arredondarDuasCasas(
                calcularValorFinal(request.getValor(), produto.getRentabilidade(), request.getPrazoMeses()));
        double rentabilidadeEfetiva = arredondarDuasCasas(
                calcularRentabilidadeEfetiva(produto.getRentabilidade(), request.getPrazoMeses()));
        LocalDateTime agora = LocalDateTime.now();

        SimulacaoEntity simulacao = new SimulacaoEntity();
        simulacao.setClienteId(request.getClienteId());
        simulacao.setProduto(produto);
        simulacao.setValorInvestido(valorInvestido);
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
                .map(this::toSimulacaoHistoricoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimulacoesPorProdutoDiaDTO> buscarSimulacoesPorProdutoPorDia(LocalDate inicio, LocalDate fim) {
        // Lógica de fallback: se o usuário não informar as datas, é assumido um período padrão de 30 dias.
        // Isso evita a necessidade de validações complexas no controller.        
        LocalDate fimConsulta = fim != null ? fim : LocalDate.now();
        LocalDate inicioConsulta = inicio != null ? inicio : fimConsulta.minusDays(30);

        if (inicioConsulta.isAfter(fimConsulta)) { // Validação básica para evitar erros na consulta.
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final.");
        }

        LocalDateTime inicioTimestamp = inicioConsulta.atStartOfDay();
        LocalDateTime fimTimestamp = fimConsulta.atTime(LocalTime.MAX);

        List<SimulacaoEntity> simulacoes = simulacaoRepository.findByDataSimulacaoBetween(inicioTimestamp, fimTimestamp);

        Map<ProdutoEntity, Map<LocalDate, List<SimulacaoEntity>>> agrupado = new java.util.HashMap<>();
        for (SimulacaoEntity sim : simulacoes) {
            agrupado.computeIfAbsent(sim.getProduto(), k -> new java.util.HashMap<>())
                    .computeIfAbsent(sim.getDataSimulacao().toLocalDate(), k -> new ArrayList<>())
                    .add(sim);
        }

        List<SimulacoesPorProdutoDiaDTO> resposta = new ArrayList<>();
        for (Map.Entry<ProdutoEntity, Map<LocalDate, List<SimulacaoEntity>>> entryProduto : agrupado.entrySet()) {
            ProdutoEntity produto = entryProduto.getKey();
            for (Map.Entry<LocalDate, List<SimulacaoEntity>> entryDia : entryProduto.getValue().entrySet()) {
                List<SimulacaoEntity> lista = entryDia.getValue();
                double mediaValorFinal = lista.stream()
                        .mapToDouble(SimulacaoEntity::getValorFinal)
                        .average()
                        .orElse(0.0);

                SimulacoesPorProdutoDiaDTO dto = new SimulacoesPorProdutoDiaDTO();
                dto.setProduto(produto.getNome());
                dto.setData(entryDia.getKey().toString());
                dto.setQuantidadeSimulacoes(lista.size());
                dto.setMediaValorFinal(arredondarDuasCasas(mediaValorFinal));
                resposta.add(dto);
            }
        }

        resposta.sort(Comparator
                .comparing(SimulacoesPorProdutoDiaDTO::getData).reversed()
                .thenComparing(SimulacoesPorProdutoDiaDTO::getProduto));

        return resposta;
    }

    private double arredondarDuasCasas(double valor) {
        // Usando BigDecimal para cálculos financeiros para evitar os problemas
        // de precisão inerentes aos tipos float/double.
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Converte a entidade Simulacao para seu DTO de histórico.
     * Movido do DtoMapper para manter a lógica de mapeamento junto ao serviço que a utiliza.
     */

    private SimulacaoHistoricoDTO toSimulacaoHistoricoDTO(SimulacaoEntity simulacao) {
        SimulacaoHistoricoDTO dto = new SimulacaoHistoricoDTO();
        dto.setId(simulacao.getId());
        dto.setClienteId(simulacao.getClienteId());
        dto.setProduto(simulacao.getProduto().getNome());
        dto.setValorInvestido(arredondarDuasCasas(simulacao.getValorInvestido()));
        dto.setValorFinal(arredondarDuasCasas(simulacao.getValorFinal()));
        dto.setPrazoMeses(simulacao.getPrazoMeses());
        dto.setDataSimulacao(simulacao.getDataSimulacao());
        return dto;
    }

    private void registrarInvestimentoHistorico(SimularInvestimentoRequestDTO request,
                                                ProdutoEntity produto,
                                                LocalDateTime dataHoraSimulacao) {
        InvestimentoClienteEntity investimento = new InvestimentoClienteEntity();
        investimento.setClienteId(request.getClienteId());
        investimento.setTipo(produto.getTipo());
        investimento.setValor(request.getValor());
        investimento.setRentabilidade(produto.getRentabilidade());
        investimento.setData(dataHoraSimulacao.toLocalDate()); // Garante que apenas a data seja salva
        investimentoRepository.save(investimento);
    }

    /**
     * Fórmula baseada em rentabilidade anual simples.
     * OBS: Não foi definido nos requisitos a rentabilidade ou juros de cada produto/investimento.
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
