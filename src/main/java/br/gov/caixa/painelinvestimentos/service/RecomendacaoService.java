package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.model.dto.PerfilRiscoResponseDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecomendacaoService {

    private final PerfilRiscoService perfilRiscoService;
    private final ProdutoRepository produtoRepository;

    public RecomendacaoService(PerfilRiscoService perfilRiscoService,
                               ProdutoRepository produtoRepository) {
        this.perfilRiscoService = perfilRiscoService;
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoRecomendadoDTO> recomendar(Long clienteId) {
        PerfilRiscoResponseDTO perfil = perfilRiscoService.calcularPerfil(clienteId);

        List<ProdutoEntity> produtos = produtoRepository.findAll();
        List<ProdutoRecomendadoDTO> recomendados = new ArrayList<>();

        for (ProdutoEntity p : produtos) {
            if (produtoCompatívelComPerfil(perfil.getPerfil(), p)) {
                ProdutoRecomendadoDTO dto = new ProdutoRecomendadoDTO();
                dto.setId(p.getId());
                dto.setNome(p.getNome());
                dto.setTipo(p.getTipo());
                dto.setRisco(p.getRisco());
                dto.setRentabilidade(p.getRentabilidade());

                // pontuação = rentabilidade * fator de match com o perfil
                dto.setPontuacao(calcularPontuacao(perfil.getPerfil(), p));

                recomendados.add(dto);
            }
        }

        recomendados.sort(Comparator.comparing(ProdutoRecomendadoDTO::getPontuacao).reversed());
        return recomendados;
    }

    private boolean produtoCompatívelComPerfil(PerfilRisco perfil, ProdutoEntity produto) {
        return switch (perfil) {
            case CONSERVADOR -> produto.getRisco().equals("BAIXO");
            case MODERADO -> produto.getRisco().equals("BAIXO") || produto.getRisco().equals("MEDIO");
            case AGRESSIVO -> true;
        };
    }

    private double calcularPontuacao(PerfilRisco perfil, ProdutoEntity produto) {
        double base = produto.getRentabilidade();

        return switch (perfil) {
            case CONSERVADOR -> base * 0.8;
            case MODERADO -> base * 1.0;
            case AGRESSIVO -> base * 1.2;
        };
    }
}