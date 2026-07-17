package io.coreplatform.openapi.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    /** Default timeout for backend requests in milliseconds */
    private int defaultTimeout = 30000;

    /** Gateway proxy endpoint prefix */
    private String proxyPrefix = "/gateway";
}