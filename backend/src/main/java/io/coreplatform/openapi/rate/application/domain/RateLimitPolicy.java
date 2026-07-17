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
public class RateLimitPolicy {

    private Long id;
    private String name;
    private String scope;
    private Long scopeId;
    private String algorithm;
    private Integer limitValue;
    private Integer refillRate;
    private Integer refillPeriod;
    private String status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
