package io.coreplatform.openapi.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "core-openapi.security")
public class SecurityRuntimeProperties {

    private JwtConfig jwt = new JwtConfig();
    private RiskControlConfig riskControl = new RiskControlConfig();

    @Data
    public static class JwtConfig {
        private String secret = "default-jwt-secret-change-in-production-min-32-chars";
        private long expiration = 86400000;
    }

    @Data
    public static class RiskControlConfig {
        private RateLimitConfig rateLimit = new RateLimitConfig();

        @Data
        public static class RateLimitConfig {
            private boolean enabled = true;
            private int maxRequests = 1000;
            private int windowSeconds = 60;
        }
    }
}