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
public class ApiKeyUsageLog {
    private Long id;
    private Long apiKeyId;
    private Long apiId;
    private String requestId;
    private String ip;
    private LocalDateTime timestamp;
    private String status;
}