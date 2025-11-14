package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoResumoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoRequestDTO;
import br.gov.caixa.painelinvestimentos.model.dto.SimulacaoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;

    public SimulacaoService(ProdutoRepository produtoRepository,
                            SimulacaoRepository simulacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
    }

    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO request) {

        ProdutoEntity produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        // Regra de simulação:
        // valorFinal = valorInvestido * (1 + rentabilidade/100) ^ prazoMeses
        double taxa = produto.getRentabilidade() / 100.0;
        double valorFinal = request.getValorInvestido() * Math.pow(1 + taxa, request.getPrazoMeses());

        SimulacaoEntity simulacao = new SimulacaoEntity();
        simulacao.setClienteId(request.getClienteId());
        simulacao.setProduto(produto);
        simulacao.setValorInvestido(request.getValorInvestido());
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(request.getPrazoMeses());
        simulacao.setDataSimulacao(LocalDateTime.now());

        SimulacaoEntity salva = simulacaoRepository.save(simulacao);

        ProdutoResumoDTO produtoResumo = new ProdutoResumoDTO();
        produtoResumo.setId(produto.getId());
        produtoResumo.setNome(produto.getNome());
        produtoResumo.setTipo(produto.getTipo());
        produtoResumo.setRisco(produto.getRisco());
        produtoResumo.setRentabilidade(produto.getRentabilidade());

        SimulacaoResponseDTO resposta = new SimulacaoResponseDTO();
        resposta.setSimulacaoId(salva.getId());
        resposta.setClienteId(salva.getClienteId());
        resposta.setProduto(produtoResumo);
        resposta.setValorInvestido(salva.getValorInvestido());
        resposta.setValorFinal(salva.getValorFinal());
        resposta.setPrazoMeses(salva.getPrazoMeses());
        resposta.setDataSimulacao(salva.getDataSimulacao());

        return resposta;
    }
}
