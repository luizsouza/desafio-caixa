package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.InvestimentoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
import br.gov.caixa.painelinvestimentos.repository.InvestimentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestimentoServiceTest {

    @Mock
    private InvestimentoRepository investimentoRepository;

    @InjectMocks
    private InvestimentoService investimentoService;

    @Test
    @DisplayName("Deve converter entidades em DTO mantendo campos essenciais")
    void shouldMapEntitiesToDto() {
        InvestimentoClienteEntity entity = new InvestimentoClienteEntity();
        entity.setId(10L);
        entity.setClienteId(123L);
        entity.setTipo("CDB");
        entity.setValor(1000.0);
        entity.setRentabilidade(0.12);
        entity.setData(LocalDate.parse("2025-01-01"));

        when(investimentoRepository.findByClienteIdOrderByDataDesc(123L))
                .thenReturn(List.of(entity));

        List<InvestimentoHistoricoDTO> result = investimentoService.listarPorCliente(123L);

        assertThat(result).hasSize(1);
        InvestimentoHistoricoDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTipo()).isEqualTo("CDB");
        assertThat(dto.getValor()).isEqualTo(1000.0);
        assertThat(dto.getRentabilidade()).isEqualTo(0.12);
        assertThat(dto.getData()).isEqualTo(LocalDate.parse("2025-01-01"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não possui histórico")
    void shouldReturnEmptyWhenNoRecords() {
        when(investimentoRepository.findByClienteIdOrderByDataDesc(999L))
                .thenReturn(List.of());

        List<InvestimentoHistoricoDTO> result = investimentoService.listarPorCliente(999L);

        assertThat(result).isEmpty();
    }
}
