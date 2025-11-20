package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.InvestimentoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.mapper.DtoMapper;
import br.gov.caixa.painelinvestimentos.repository.InvestimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepository;

    public InvestimentoService(InvestimentoRepository investimentoRepository) {
        this.investimentoRepository = investimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<InvestimentoHistoricoDTO> listarPorCliente(Long clienteId) {
        return investimentoRepository.findByClienteIdOrderByDataDesc(clienteId)
                .stream()
                .map(DtoMapper::toInvestimentoHistoricoDTO)
                .collect(Collectors.toList());
    }
}
