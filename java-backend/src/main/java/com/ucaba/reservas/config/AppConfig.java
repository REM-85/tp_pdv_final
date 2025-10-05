package com.ucaba.reservas.config;

import java.util.Collections;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private final String headerName;

    public AppConfig(@Value("${reservas.correlation-header}") String headerName) {
        this.headerName = headerName;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.additionalInterceptors((request, body, execution) -> {
                    String corrId = MDC.get(headerName);
                    if (corrId != null) {
                        request.getHeaders().put(headerName, Collections.singletonList(corrId));
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}

