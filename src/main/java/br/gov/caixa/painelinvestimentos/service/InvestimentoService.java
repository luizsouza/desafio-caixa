package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.InvestimentoHistoricoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.InvestimentoClienteEntity;
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
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private InvestimentoHistoricoDTO toDTO(InvestimentoClienteEntity entity) {
        InvestimentoHistoricoDTO dto = new InvestimentoHistoricoDTO();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setValor(entity.getValor());
        dto.setRentabilidade(entity.getRentabilidade());
        dto.setData(entity.getData());
        return dto;
    }
}
