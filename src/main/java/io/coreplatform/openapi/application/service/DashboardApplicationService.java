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
    private final UserApplicationService userApplicationService;
    private final ApplicationApplicationService applicationApplicationService;
    private final ApiKeyApplicationService apiKeyApplicationService;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long serviceCount = serviceRepository.findPage(1, 1, null).getTotal();
        long definitionCount = definitionRepository.findPage(1, 1, null, null, null).getTotal();
        long publishedCount = definitionRepository.countByStatus("PUBLISHED");
        long userCount = userApplicationService.count();
        long applicationCount = applicationApplicationService.count();
        long activeKeyCount = apiKeyApplicationService.countActive();

        stats.put("serviceCount", serviceCount);
        stats.put("definitionCount", definitionCount);
        stats.put("publishedCount", publishedCount);
        stats.put("userCount", userCount);
        stats.put("applicationCount", applicationCount);
        stats.put("activeKeyCount", activeKeyCount);
        return stats;
    }
}