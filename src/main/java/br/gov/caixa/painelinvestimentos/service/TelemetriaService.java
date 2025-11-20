package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaPeriodoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaResponseDTO;
import br.gov.caixa.painelinvestimentos.model.dto.TelemetriaServicoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.TelemetriaEntity;
import br.gov.caixa.painelinvestimentos.model.mapper.DtoMapper;
import br.gov.caixa.painelinvestimentos.repository.TelemetriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public TelemetriaService(TelemetriaRepository telemetriaRepository) {
        this.telemetriaRepository = telemetriaRepository;
    }

    public void registrarExecucao(String endpoint, long duracaoMs) {
        TelemetriaEntity telemetria = new TelemetriaEntity();
        telemetria.setEndpoint(endpoint);
        telemetria.setTempoRespostaMs(duracaoMs);
        telemetria.setTimestamp(LocalDateTime.now());
        telemetriaRepository.save(telemetria);
    }

    /**
     * Obtém a telemetria
     *
     * - Se início e fim forem informados → usa o período enviado
     * - Se não forem informados → últimos 30 dias
     */
    public TelemetriaResponseDTO obterTelemetria(LocalDate inicio, LocalDate fim) {

        // Se não vierem parâmetros → últimos 30 dias
        if (inicio == null || fim == null) {
            fim = LocalDate.now();
            inicio = fim.minusDays(30);
        }

        LocalDateTime iniTimestamp = inicio.atStartOfDay();
        LocalDateTime fimTimestamp = fim.atTime(23, 59, 59);

        // Consulta o banco
        List<TelemetriaEntity> registros =
                telemetriaRepository.findByTimestampBetween(iniTimestamp, fimTimestamp);

        // Agrupar por endpoint ("/simular-investimento", "/produtos", etc.)
        List<TelemetriaServicoDTO> servicos =
                registros.stream()
                        .collect(Collectors.groupingBy(TelemetriaEntity::getEndpoint))
                        .entrySet()
                        .stream()
                        .map(entry -> DtoMapper.toTelemetriaServicoDTO(entry.getKey(), entry.getValue()))
                        .sorted(Comparator.comparing(TelemetriaServicoDTO::getNome))
                        .collect(Collectors.toList());

        // DTO do período
        TelemetriaPeriodoDTO periodoDTO = new TelemetriaPeriodoDTO();
        periodoDTO.setInicio(inicio.toString());
        periodoDTO.setFim(fim.toString());

        // DTO de resposta final
        TelemetriaResponseDTO response = new TelemetriaResponseDTO();
        response.setPeriodo(periodoDTO);
        response.setServicos(servicos);

        return response;
    }
}
