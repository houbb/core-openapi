package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialResponse {
    private Long id;
    private String name;
    private String keyPrefix;
    private String rawKey;
    private String environment;
    private String status;
    private LocalDateTime expireTime;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createdTime;
}