package br.gov.caixa.painelinvestimentos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Set;


// Configura o Swagger para expor o botão Authorize

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .name(BEARER_AUTH_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME, bearerScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
    }

    @Bean
    public OpenApiCustomizer currentDateParameterCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            String today = LocalDate.now().toString();
            Set<String> parametrosComDataDefault = Set.of("fim", "data");

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        if (operation.getParameters() == null) {
                            return;
                        }
                        for (Parameter parameter : operation.getParameters()) {
                            if (!parametrosComDataDefault.contains(parameter.getName())) {
                                continue;
                            }
                            Schema<?> schema = parameter.getSchema();
                            if (schema == null) {
                                schema = new StringSchema();
                                parameter.setSchema(schema);
                            }
                            schema.setExample(today);
                        }
                    })
            );
        };
    }
}
