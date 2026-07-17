package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.security.config.SecurityRuntimeProperties;
import io.coreplatform.openapi.security.domain.AuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticator {

    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtAuthenticator(SecurityRuntimeProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(
                properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    /**
     * Validate a JWT token and extract authentication info.
     *
     * @param token the raw JWT token string
     * @return AuthenticationToken with claims extracted
     * @throws JwtAuthenticationException if token is invalid or expired
     */
    public AuthenticationToken authenticate(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

            String subject = claims.getSubject();
            Long applicationId = claims.get("applicationId", Long.class);
            Long userId = claims.get("userId", Long.class);
            String tenantId = claims.get("tenantId", String.class);

            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);

            return AuthenticationToken.builder()
                    .tokenType("JWT")
                    .principal(subject)
                    .applicationId(applicationId)
                    .userId(userId)
                    .tenantId(tenantId != null ? tenantId : "")
                    .permissions(permissions != null ? permissions : List.of())
                    .build();

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT令牌已过期", e);
        } catch (SignatureException | MalformedJwtException e) {
            log.warn("JWT token invalid: {}", e.getMessage());
            throw new JwtAuthenticationException("无效的JWT令牌", e);
        } catch (Exception e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT令牌验证失败", e);
        }
    }

    /**
     * Check if a token looks like a JWT (starts with eyJ).
     */
    public static boolean looksLikeJwt(String token) {
        return token != null && token.startsWith("eyJ");
    }

    /**
     * Exception thrown when JWT authentication fails.
     */
    public static class JwtAuthenticationException extends RuntimeException {
        public JwtAuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}