package br.gov.caixa.painelinvestimentos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class PainelInvestimentosApplicationSmokeTest {

    @Test
    @DisplayName("Aplicação deve subir contexto sem erro")
    void shouldStartApplicationContext() {
        try (ConfigurableApplicationContext ctx = new SpringApplicationBuilder(PainelInvestimentosApplication.class)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=0")
                .run()) {
            assertThat(ctx).isNotNull();
            assertThat(ctx.isActive()).isTrue();
        }
    }
}
