package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

    private final ServiceRepository serviceRepository;
    private final DefinitionRepository definitionRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Count services: this is a simplified approach — in production use COUNT queries
        long serviceCount = serviceRepository.findPage(1, 1, null).getTotal();
        long definitionCount = definitionRepository.findPage(1, 1, null, null, null).getTotal();
        long publishedCount = definitionRepository.countByStatus("PUBLISHED");

        stats.put("serviceCount", serviceCount);
        stats.put("definitionCount", definitionCount);
        stats.put("publishedCount", publishedCount);
        return stats;
    }
}
