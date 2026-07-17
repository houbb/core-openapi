package io.coreplatform.openapi.rate.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitUsage {

    private Long id;
    private Long policyId;
    private Long apiId;
    private Long applicationId;
    private String identityId;
    private Integer requestCount;
    private Integer blockedCount;
    private LocalDateTime recordedAt;
    private LocalDateTime createTime;
}