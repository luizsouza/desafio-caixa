package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.exception.SemDadosPerfilException;
import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.SimulacaoEntity;
import br.gov.caixa.painelinvestimentos.repository.SimulacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilRiscoServiceTest {

    @Mock
    private SimulacaoRepository simulacaoRepository;

    @InjectMocks
    private PerfilRiscoService perfilRiscoService;

    @Test
    @DisplayName("Sem histórico o serviço deve informar que não há dados suficientes")
    void shouldFailWhenNoHistory() {
        when(simulacaoRepository.findByClienteId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> perfilRiscoService.calcularPerfil(1L))
                .isInstanceOf(SemDadosPerfilException.class)
                .hasMessageContaining("1");
    }

    @Test
    @DisplayName("Volumes altos, frequência intensa e prazos longos levam ao perfil agressivo")
    void shouldReturnAggressiveProfile() {
        when(simulacaoRepository.findByClienteId(2L))
                .thenReturn(gerarSimulacoes(6, 60000, 24));

        PerfilRiscoResponseDTO dto = perfilRiscoService.calcularPerfil(2L);

        assertThat(dto.getPerfil()).isEqualTo(PerfilRisco.AGRESSIVO);
        assertThat(dto.getPontuacaoVolume()).isEqualTo(30);
        assertThat(dto.getPontuacaoFrequencia()).isEqualTo(30);
        assertThat(dto.getPontuacaoLiquidez()).isEqualTo(30);
        assertThat(dto.getDescricao()).contains("Agressivo");
    }

    private List<SimulacaoEntity> gerarSimulacoes(int quantidade, double valor, int prazo) {
        List<SimulacaoEntity> simulacoes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            SimulacaoEntity entity = new SimulacaoEntity();
            entity.setValorInvestido(valor);
            entity.setPrazoMeses(prazo);
            entity.setDataSimulacao(LocalDateTime.now());
            simulacoes.add(entity);
        }
        return simulacoes;
    }
}
