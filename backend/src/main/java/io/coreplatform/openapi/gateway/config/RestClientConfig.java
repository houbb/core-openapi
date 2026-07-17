package io.coreplatform.openapi.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(GatewayProperties gatewayProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // 5 seconds
        requestFactory.setReadTimeout(gatewayProperties.getDefaultTimeout()); // from config

        return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultStatusHandler(status -> status.is5xxServerError(), (request, response) -> {
                    // Let the caller handle 5xx — don't throw by default
                    // Response body is still available via toEntity()
                });
    }
}