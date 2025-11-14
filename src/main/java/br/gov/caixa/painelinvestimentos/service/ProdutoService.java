package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoDTO> listarTodos() {
        List<ProdutoEntity> entidades = produtoRepository.findAll();
        return entidades.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ProdutoDTO toDTO(ProdutoEntity entity) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTipo(entity.getTipo());
        dto.setRisco(entity.getRisco());
        dto.setRentabilidade(entity.getRentabilidade());
        dto.setMinValor(entity.getMinValor());
        dto.setMaxValor(entity.getMaxValor());
        dto.setMinPrazo(entity.getMinPrazo());
        dto.setMaxPrazo(entity.getMaxPrazo());
        return dto;
    }
}