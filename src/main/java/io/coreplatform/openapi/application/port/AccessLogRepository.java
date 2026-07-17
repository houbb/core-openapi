package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.AccessLog;

public interface AccessLogRepository {

    void save(AccessLog accessLog);

    IPage<AccessLog> findPage(long page, long size, Long apiId);

    Long countToday();
}