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
            throw new SemDadosPerfilException(
                    "Nao encontramos simulacoes para o cliente " + clienteId
                            + ". Realize ao menos uma simulacao para obter o perfil de risco.");
        }

        int pontVolume = calcularPontuacaoVolume(simulacoes);
        int pontFreq = calcularPontuacaoFrequencia(simulacoes);
        int pontLiquidez = calcularPontuacaoLiquidez(simulacoes);

        int total = pontVolume + pontFreq + pontLiquidez;

        PerfilRisco perfil;
        if (total <= 40) {
            perfil = PerfilRisco.CONSERVADOR;
        } else if (total <= 70) {
            perfil = PerfilRisco.MODERADO;
        } else {
            perfil = PerfilRisco.AGRESSIVO;
        }

        PerfilRiscoResponseDTO dto = new PerfilRiscoResponseDTO();
        dto.setClienteId(clienteId);
        dto.setPerfil(perfil);
        dto.setPontuacaoVolume(pontVolume);
        dto.setPontuacaoFrequencia(pontFreq);
        dto.setPontuacaoLiquidez(pontLiquidez);
        dto.setPontuacaoTotal(total);
        dto.setDescricao(descricaoPerfil(perfil));
        return dto;
    }

    private int calcularPontuacaoVolume(List<SimulacaoEntity> simulacoes) {
        double volumeTotal = 0.0;
        for (SimulacaoEntity s : simulacoes) {
            volumeTotal += s.getValorInvestido();
        }

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

        if (quantidade <= 2) {
            return 10;
        } else if (quantidade <= 5) {
            return 20;
        } else {
            return 30;
        }
    }

    private int calcularPontuacaoLiquidez(List<SimulacaoEntity> simulacoes) {
        double somaPrazo = 0.0;
        for (SimulacaoEntity s : simulacoes) {
            somaPrazo += s.getPrazoMeses();
        }
        double prazoMedio = somaPrazo / simulacoes.size();

        if (prazoMedio <= 6) {
            return 10;
        } else if (prazoMedio <= 18) {
            return 20;
        } else {
            return 30;
        }
    }

    private String descricaoPerfil(PerfilRisco perfil) {
        return switch (perfil) {
            case CONSERVADOR -> "Conservador: baixa movimentacao e foco em liquidez.";
            case MODERADO -> "Moderado: equilibrio entre liquidez e rentabilidade.";
            case AGRESSIVO -> "Agressivo: busca por maior rentabilidade assumindo mais risco.";
        };
    }
}
