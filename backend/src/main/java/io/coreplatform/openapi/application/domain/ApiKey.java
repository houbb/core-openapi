package io.coreplatform.openapi.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {
    private Long id;
    private Long applicationId;
    private String keyPrefix;
    private String keyHash;
    private String name;
    private String environment;
    private String status;
    private LocalDateTime expireTime;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createdTime;
    private String tenantId;

    /** Raw key value, ONLY populated at creation time. Never stored in DB. */
    @Builder.Default
    private String rawKey = null;
}
