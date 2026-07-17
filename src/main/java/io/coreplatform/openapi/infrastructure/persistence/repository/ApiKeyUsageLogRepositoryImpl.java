package io.coreplatform.openapi.infrastructure.persistence.repository;

import io.coreplatform.openapi.application.domain.ApiKeyUsageLog;
import io.coreplatform.openapi.application.port.ApiKeyUsageLogRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.ApiKeyUsageLogEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.ApiKeyUsageLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiKeyUsageLogRepositoryImpl implements ApiKeyUsageLogRepository {

    private final ApiKeyUsageLogMapper apiKeyUsageLogMapper;

    @Override
    public void save(ApiKeyUsageLog log) {
        ApiKeyUsageLogEntity entity = toEntity(log);
        apiKeyUsageLogMapper.insert(entity);
    }

    // ---- Entity <-> Domain conversion ----

    private ApiKeyUsageLogEntity toEntity(ApiKeyUsageLog domain) {
        ApiKeyUsageLogEntity entity = new ApiKeyUsageLogEntity();
        entity.setApiKeyId(domain.getApiKeyId());
        entity.setApiId(domain.getApiId());
        entity.setRequestId(domain.getRequestId());
        entity.setIp(domain.getIp());
        entity.setTimestamp(domain.getTimestamp());
        entity.setStatus(domain.getStatus());
        return entity;
    }
}