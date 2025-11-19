package br.gov.caixa.painelinvestimentos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TelemetriaInterceptor telemetriaInterceptor;

    @Autowired
    public WebConfig(TelemetriaInterceptor telemetriaInterceptor) {
        this.telemetriaInterceptor = telemetriaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Aplica telemetria em TODOS os endpoints da API
        registry.addInterceptor(telemetriaInterceptor)
                .addPathPatterns("/**");
    }
}
