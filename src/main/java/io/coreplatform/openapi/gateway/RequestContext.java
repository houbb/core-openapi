package io.coreplatform.openapi.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestContext {
    private String requestId;
    private String traceId;
    private String clientId;
    private String userId;
    private String tenantId;
    private LocalDateTime timestamp;
}