package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.exception.SemDadosPerfilException;
import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerfilRiscoService {

    private final SimulacaoRepository simulacaoRepository;

    public PerfilRiscoService(SimulacaoRepository simulacaoRepository) {
        this.simulacaoRepository = simulacaoRepository;
    }

    public PerfilRiscoResponseDTO calcularPerfil(Long clienteId) {
        List<SimulacaoEntity> simulacoes = simulacaoRepository.findByClienteId(clienteId);

        if (simulacoes.isEmpty()) {
            // Regra de negócio: O perfil de risco só pode ser calculado se o cliente já interagiu com a plataforma.
            throw new SemDadosPerfilException(
                    "Não encontramos simulações para o cliente " + clienteId
                            + ". Realize ao menos uma simulação para obter o perfil de risco.");
        }

        int pontVolume = calcularPontuacaoVolume(simulacoes);
        int pontFreq = calcularPontuacaoFrequencia(simulacoes);
        int pontLiquidez = calcularPontuacaoLiquidez(simulacoes);

        int pontuacaoTotal = pontVolume + pontFreq + pontLiquidez;
        PerfilRisco perfil = determinarPerfilPorPontuacao(pontuacaoTotal);

        PerfilRiscoResponseDTO dto = new PerfilRiscoResponseDTO();
        dto.setClienteId(clienteId);
        dto.setPerfil(perfil);
        dto.setPontuacaoVolume(pontVolume);
        dto.setPontuacaoFrequencia(pontFreq);
        dto.setPontuacaoLiquidez(pontLiquidez);
        dto.setPontuacaoTotal(pontuacaoTotal);
        dto.setDescricao(descricaoPerfil(perfil));
        return dto;
    }

    private int calcularPontuacaoVolume(List<SimulacaoEntity> simulacoes) {
        double volumeTotal = simulacoes.stream()
                .mapToDouble(SimulacaoEntity::getValorInvestido).sum();

        // Faixas de pontuação (10k, 50k) em reais.
        if (volumeTotal < 10_000.0) {
            return 10;
        } else if (volumeTotal <= 50_000.0) {
            return 20;
        } else {
            return 30;
        }
    }

    private int calcularPontuacaoFrequencia(List<SimulacaoEntity> simulacoes) {
        int quantidade = simulacoes.size();

        // A ideia aqui é que clientes que simulam mais vezes são mais engajados/propensos a risco.
        if (quantidade <= 2) {
            return 10;
        } else if (quantidade <= 5) {
            return 20;
        } else {
            return 30;
        }
    }

    private int calcularPontuacaoLiquidez(List<SimulacaoEntity> simulacoes) {
        double prazoMedio = simulacoes.stream()
                .mapToInt(SimulacaoEntity::getPrazoMeses)
                .average().orElse(0.0);

        // Prazos mais longos indicam menor necessidade de liquidez e, portanto, maior apetite a risco.
        if (prazoMedio <= 6) {
            return 10;
        } else if (prazoMedio <= 18) {
            return 20;
        } else {
            return 30;
        }
    }

    private PerfilRisco determinarPerfilPorPontuacao(int pontuacaoTotal) {
        // Faixas de corte para definição do perfil.
        if (pontuacaoTotal <= 40) {
            return PerfilRisco.CONSERVADOR;
        } else if (pontuacaoTotal <= 70) {
            return PerfilRisco.MODERADO;
        } else {
            return PerfilRisco.AGRESSIVO;
        }
    }

    private String descricaoPerfil(PerfilRisco perfil) {
        // Textos amigáveis
        return switch (perfil) {
            case CONSERVADOR -> "Conservador: baixa movimentação e foco em liquidez.";
            case MODERADO -> "Moderado: equilíbrio entre liquidez e rentabilidade.";
            case AGRESSIVO -> "Agressivo: busca por maior rentabilidade assumindo mais risco.";
        };
    }
}