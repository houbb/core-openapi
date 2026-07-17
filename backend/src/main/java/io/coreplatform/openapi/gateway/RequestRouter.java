package io.coreplatform.openapi.gateway;

import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.domain.Route;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestRouter {

    private final RouteRepository routeRepository;
    private final DefinitionRepository definitionRepository;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Match an incoming request to a route configuration.
     *
     * Steps:
     * 1. Get all active routes
     * 2. For each route, look up its API definition by api_id
     * 3. Only match PUBLISHED definitions with matching HTTP method
     * 4. Convert definition path (e.g. /v1/users/{id}) to Ant pattern (/v1/users/*)
     * 5. Match against the actual request path
     * 6. Return the matching route or throw GatewayException
     */
    public RouteResult resolve(String requestPath, String httpMethod) {
        List<Route> activeRoutes = routeRepository.findActiveRoutes();

        for (Route route : activeRoutes) {
            Optional<Definition> defOpt = definitionRepository.findById(route.getApiId());

            if (defOpt.isPresent()) {
                Definition def = defOpt.get();

                // Only match published definitions
                if (!"PUBLISHED".equals(def.getStatus())) {
                    continue;
                }

                // Match HTTP method
                if (!def.getHttpMethod().equalsIgnoreCase(httpMethod)) {
                    continue;
                }

                // Convert {param} pattern to Ant-style * pattern
                String antPattern = convertToAntPattern(def.getPath());
                log.debug("Matching: requestPath={}, antPattern={}, targetUrl={}",
                        requestPath, antPattern, route.getTargetUrl());

                if (pathMatcher.match(antPattern, requestPath)) {
                    // Build the actual backend URL
                    String backendPath = requestPath;
                    String fullTargetUrl = route.getTargetUrl() + backendPath;

                    log.info("Route matched: {} → {}", requestPath, fullTargetUrl);
                    return new RouteResult(route, def, fullTargetUrl);
                }
            }
        }

        log.warn("No route found for: {} {}", httpMethod, requestPath);
        throw new GatewayException(GatewayErrorCode.ROUTE_NOT_FOUND);
    }

    /**
     * Convert REST path pattern to AntMatcher pattern.
     * e.g. /v1/users/{id} → /v1/users/*
     *      /v1/users/{id}/posts/{postId} → /v1/users/* /posts/*
     */
    private String convertToAntPattern(String definitionPath) {
        return definitionPath.replaceAll("\\{[^}]+\\}", "*");
    }

    /**
     * Record class for matched route result.
     */
    public record RouteResult(Route route, Definition definition, String fullTargetUrl) {
    }
}