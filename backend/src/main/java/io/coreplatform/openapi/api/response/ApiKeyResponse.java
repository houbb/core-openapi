package io.coreplatform.openapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponse {
    private Long id;
    private Long applicationId;
    private String keyPrefix;
    private String name;
    private String environment;
    private String status;
    private LocalDateTime expireTime;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createdTime;

    /** Only populated on creation — the raw key value. Never returned for existing keys. */
    private String rawKey;
}