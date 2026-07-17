package io.coreplatform.openapi.security.service;

import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.domain.ApplicationPermission;
import io.coreplatform.openapi.application.domain.Permission;
import io.coreplatform.openapi.application.port.ApplicationPermissionRepository;
import io.coreplatform.openapi.application.port.PermissionRepository;
import io.coreplatform.openapi.gateway.GatewayErrorCode;
import io.coreplatform.openapi.gateway.GatewayException;
import io.coreplatform.openapi.gateway.KeyValidator;
import io.coreplatform.openapi.gateway.RequestRouter;
import io.coreplatform.openapi.security.domain.AuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationService {

    private final KeyValidator keyValidator;
    private final JwtAuthenticator jwtAuthenticator;
    private final ApplicationPermissionRepository applicationPermissionRepository;
    private final PermissionRepository permissionRepository;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Authenticate a request using API Key or JWT.
     *
     * @param request     the HTTP servlet request
     * @param matchedRoute the matched route (may be null)
     * @return AuthenticationToken with identity and permissions
     * @throws GatewayException if authentication fails
     */
    public AuthenticationToken authenticate(HttpServletRequest request,
                                             RequestRouter.RouteResult matchedRoute) {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "缺少Authorization头");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new GatewayException(GatewayErrorCode.INVALID_API_KEY, "认证令牌为空");
        }

        if (JwtAuthenticator.looksLikeJwt(token)) {
            return authenticateJwt(token);
        } else {
            return authenticateApiKey(token, request, matchedRoute);
        }
    }

    private AuthenticationToken authenticateJwt(String token) {
        try {
            return jwtAuthenticator.authenticate(token);
        } catch (JwtAuthenticator.JwtAuthenticationException e) {
            if (e.getMessage().contains("过期")) {
                throw new GatewayException(GatewayErrorCode.JWT_EXPIRED, e.getMessage());
            }
            throw new GatewayException(GatewayErrorCode.INVALID_JWT, e.getMessage());
        }
    }

    private AuthenticationToken authenticateApiKey(String rawKey,
                                                    HttpServletRequest request,
                                                    RequestRouter.RouteResult matchedRoute) {
        // Delegate to KeyValidator for key extraction, hash, lookup, status checks
        KeyValidator.AuthResult authResult = keyValidator.validate(request, matchedRoute);

        ApiKey apiKey = authResult.apiKey();
        Application application = authResult.application();

        // Resolve permissions via RBAC: application -> roles -> permissions
        List<String> permissions = resolvePermissions(application.getId());

        return AuthenticationToken.builder()
                .tokenType("API_KEY")
                .principal(application.getAppCode())
                .applicationId(application.getId())
                .userId(application.getOwnerId())
                .tenantId(application.getTenantId() != null ? application.getTenantId() : "")
                .permissions(permissions)
                .build();
    }

    /**
     * Resolve effective permissions for an application.
     * First checks RBAC (application -> roles -> permissions).
     * Falls back to legacy flat application_permission mapping.
     */
    private List<String> resolvePermissions(Long applicationId) {
        List<String> permissions = new ArrayList<>();

        // Try RBAC first (will be enhanced after ApplicationRoleApplicationService is created)
        // For now, fall back to legacy flat permission mapping
        List<ApplicationPermission> appPerms = applicationPermissionRepository.findByApplicationId(applicationId);
        for (ApplicationPermission ap : appPerms) {
            Optional<Permission> perm = permissionRepository.findById(ap.getPermissionId());
            perm.ifPresent(p -> permissions.add(p.getName()));
        }

        return permissions;
    }
}