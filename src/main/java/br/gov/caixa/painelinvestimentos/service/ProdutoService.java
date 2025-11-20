package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.dto.ProdutoDTO;
import br.gov.caixa.painelinvestimentos.model.mapper.DtoMapper;
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

    /**
     * Lista todos os produtos disponíveis
     */
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(DtoMapper::toProdutoDTO)
                .collect(Collectors.toList());
    }
}

