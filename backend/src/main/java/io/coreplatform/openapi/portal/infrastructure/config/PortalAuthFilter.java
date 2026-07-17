package io.coreplatform.openapi.portal.infrastructure.config;

import io.coreplatform.openapi.api.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class PortalAuthFilter implements Filter {

    private final PortalJwtService portalJwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Public paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/portal/account/register",
            "/api/v1/portal/account/login",
            "/api/v1/portal/catalog",
            "/api/v1/portal/docs",
            "/api/v1/portal/sdk"
    );

    public PortalAuthFilter(PortalJwtService portalJwtService) {
        this.portalJwtService = portalJwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Allow public paths without auth
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Extract and validate token
        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            sendError(httpResponse, 401, "PORTAL_UNAUTHORIZED", "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        try {
            var claims = portalJwtService.validateToken(token);
            // Set userId as request attribute for downstream controllers
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            request.setAttribute("portalUserId", userId);
            request.setAttribute("portalUsername", username);
            chain.doFilter(request, response);
        } catch (Exception e) {
            sendError(httpResponse, 401, "PORTAL_TOKEN_INVALID", "Invalid or expired token: " + e.getMessage());
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    private void sendError(HttpServletResponse response, int status, String code, String detail)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiError error = ApiError.builder()
                .status(status)
                .errorCode(code)
                .detail(detail)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}