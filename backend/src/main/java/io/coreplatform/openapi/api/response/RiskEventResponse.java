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
public class RiskEventResponse {
    private Long id;
    private String identityId;
    private String riskType;
    private String severity;
    private String detail;
    private Long requestCount;
    private Integer windowSeconds;
    private Long thresholdCount;
    private String tenantId;
    private LocalDateTime createdTime;
}