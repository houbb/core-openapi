package io.coreplatform.openapi.application.port;

import io.coreplatform.openapi.application.domain.ApiKeyUsageLog;

public interface ApiKeyUsageLogRepository {

    void save(ApiKeyUsageLog log);
}
