package br.gov.caixa.painelinvestimentos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    @DisplayName("Customizer deve colocar /auth/login no topo")
    void shouldMoveAuthEndpointToTop() {
        OpenAPI api = new OpenAPI();
        Paths paths = new Paths();
        paths.addPathItem("/b", new PathItem());
        paths.addPathItem("/auth/login", new PathItem());
        paths.addPathItem("/c", new PathItem());
        api.setPaths(paths);

        config.authEndpointOnTopCustomizer().customise(api);

        assertThat(api.getPaths().entrySet().iterator().next().getKey())
                .isEqualTo("/auth/login");
    }

    @Test
    @DisplayName("Customizer deve preencher exemplos de datas em parametros fim/data")
    void shouldFillDateExamples() {
        OpenAPI api = new OpenAPI();
        Parameter fim = new Parameter().name("fim");
        Parameter outro = new Parameter().name("outro");
        PathItem item = new PathItem().get(new Operation().parameters(List.of(fim, outro)));
        Paths paths = new Paths();
        paths.addPathItem("/telemetria", item);
        api.setPaths(paths);

        config.currentDateParameterCustomizer().customise(api);

        assertThat(fim.getSchema().getExample()).isNotNull();
        assertThat(outro.getSchema()).isNull();
    }
}
