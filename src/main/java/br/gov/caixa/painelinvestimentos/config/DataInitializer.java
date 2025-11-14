package br.gov.caixa.painelinvestimentos.config;

import br.gov.caixa.painelinvestimentos.model.entity.ProdutoEntity;
import br.gov.caixa.painelinvestimentos.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(ProdutoRepository produtoRepository) {
        return args -> {
            if (produtoRepository.count() == 0) {

                ProdutoEntity conservador = new ProdutoEntity();
                conservador.setNome("CDB Conservador");
                conservador.setTipo("CDB");
                conservador.setRisco("BAIXO");
                conservador.setRentabilidade(0.8); // 0,8% ao mês
                conservador.setMinValor(1000.0);
                conservador.setMaxValor(100000.0);
                conservador.setMinPrazo(6);
                conservador.setMaxPrazo(60);
                produtoRepository.save(conservador);

                ProdutoEntity moderado = new ProdutoEntity();
                moderado.setNome("Fundo Moderado");
                moderado.setTipo("FUNDO");
                moderado.setRisco("MEDIO");
                moderado.setRentabilidade(1.2); // 1,2% ao mês
                moderado.setMinValor(5000.0);
                moderado.setMaxValor(200000.0);
                moderado.setMinPrazo(12);
                moderado.setMaxPrazo(120);
                produtoRepository.save(moderado);

                ProdutoEntity arrojado = new ProdutoEntity();
                arrojado.setNome("Ações Arrojadas");
                arrojado.setTipo("ACOES");
                arrojado.setRisco("ALTO");
                arrojado.setRentabilidade(2.0); // 2% ao mês
                arrojado.setMinValor(10000.0);
                arrojado.setMaxValor(500000.0);
                arrojado.setMinPrazo(24);
                arrojado.setMaxPrazo(240);
                produtoRepository.save(arrojado);
            }
        };
    }
}
