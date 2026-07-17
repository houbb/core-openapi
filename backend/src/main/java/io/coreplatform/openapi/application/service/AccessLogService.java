package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.domain.AccessLog;
import io.coreplatform.openapi.application.port.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogService {

    private final AccessLogRepository accessLogRepository;

    @Async("accessLogExecutor")
    public void saveAsync(AccessLog accessLog) {
        try {
            accessLogRepository.save(accessLog);
        } catch (Exception e) {
            log.error("Failed to write access log asynchronously: requestId={}",
                    accessLog.getRequestId(), e);
        }
    }

    public void saveSync(AccessLog accessLog) {
        accessLogRepository.save(accessLog);
    }
}