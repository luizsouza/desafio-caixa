package br.gov.caixa.painelinvestimentos.service;

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

        // Sem histórico: assume conservador
        if (simulacoes.isEmpty()) {
            PerfilRiscoResponseDTO dto = new PerfilRiscoResponseDTO();
            dto.setClienteId(clienteId);
            dto.setPerfil(PerfilRisco.CONSERVADOR);
            dto.setPontuacaoVolume(0);
            dto.setPontuacaoFrequencia(0);
            dto.setPontuacaoLiquidez(0);
            dto.setPontuacaoTotal(0);
            return dto;
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
            return 10; // baixa frequência
        } else if (quantidade <= 5) {
            return 20; // média
        } else {
            return 30; // alta
        }
    }

    private int calcularPontuacaoLiquidez(List<SimulacaoEntity> simulacoes) {
        double somaPrazo = 0.0;
        for (SimulacaoEntity s : simulacoes) {
            somaPrazo += s.getPrazoMeses();
        }
        double prazoMedio = somaPrazo / simulacoes.size();

        if (prazoMedio <= 6) {
            return 10; // prefere liquidez
        } else if (prazoMedio <= 18) {
            return 20; // equilibrado
        } else {
            return 30; // aceita menos liquidez em troca de rentabilidade
        }
    }
}