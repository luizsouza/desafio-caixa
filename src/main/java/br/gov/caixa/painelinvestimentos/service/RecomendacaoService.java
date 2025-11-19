package br.gov.caixa.painelinvestimentos.service;

import br.gov.caixa.painelinvestimentos.model.PerfilRisco;
import br.gov.caixa.painelinvestimentos.model.dto.ProdutoRecomendadoDTO;
import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RecomendacaoService {

    private final ProdutoRepository produtoRepository;

    public RecomendacaoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoRecomendadoDTO> recomendarPorPerfil(String perfilTexto) {
        PerfilRisco perfil = parsePerfil(perfilTexto);
        return gerarRecomendacoes(perfil);
    }

    private List<ProdutoRecomendadoDTO> gerarRecomendacoes(PerfilRisco perfil) {
        List<ProdutoEntity> produtos = produtoRepository.findAll();
        List<ProdutoRecomendadoDTO> recomendados = new ArrayList<>();

        for (ProdutoEntity produto : produtos) {
            if (produtoCompativelComPerfil(perfil, produto)) {
                ProdutoRecomendadoDTO dto = new ProdutoRecomendadoDTO();
                dto.setId(produto.getId());
                dto.setNome(produto.getNome());
                dto.setTipo(produto.getTipo());
                dto.setRisco(produto.getRisco());
                dto.setRentabilidade(produto.getRentabilidade());
                dto.setPontuacao(calcularPontuacao(perfil, produto));
                recomendados.add(dto);
            }
        }

        recomendados.sort(Comparator.comparing(ProdutoRecomendadoDTO::getPontuacao).reversed());
        return recomendados;
    }

    private boolean produtoCompativelComPerfil(PerfilRisco perfil, ProdutoEntity produto) {
        return switch (perfil) {
            case CONSERVADOR -> produto.getRisco().equalsIgnoreCase("BAIXO");
            case MODERADO -> produto.getRisco().equalsIgnoreCase("BAIXO") ||
                             produto.getRisco().equalsIgnoreCase("MEDIO");
            case AGRESSIVO -> true;
        };
    }

    private double calcularPontuacao(PerfilRisco perfil, ProdutoEntity produto) {
        double base = produto.getRentabilidade();
        return switch (perfil) {
            case CONSERVADOR -> base * 0.8;
            case MODERADO -> base;
            case AGRESSIVO -> base * 1.2;
        };
    }

    private PerfilRisco parsePerfil(String valor) {
        try {
            return PerfilRisco.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Perfil inválido. Utilize CONSERVADOR, MODERADO ou AGRESSIVO.");
        }
    }
}
