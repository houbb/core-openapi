package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.port.AccessLogRepository;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GatewayDashboardApplicationService {

    private final AccessLogRepository accessLogRepository;
    private final RouteRepository routeRepository;
    private final DefinitionRepository definitionRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long todayRequests = accessLogRepository.countToday();
        long activeRouteCount = routeRepository.countByStatus("ACTIVE");
        long publishedDefinitionCount = definitionRepository.countByStatus("PUBLISHED");

        stats.put("todayRequests", todayRequests);
        stats.put("activeRouteCount", activeRouteCount);
        stats.put("publishedDefinitionCount", publishedDefinitionCount);

        // Note: successRate, avgLatency, errorCount require more complex queries
        // These will be enhanced in future iterations with aggregation queries
        stats.put("successRate", "—");
        stats.put("avgLatency", "—");
        stats.put("errorCount", 0L);

        return stats;
    }
}