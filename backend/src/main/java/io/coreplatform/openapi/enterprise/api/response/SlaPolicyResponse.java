package io.coreplatform.openapi.enterprise.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlaPolicyResponse {
    private Long id;
    private Long organizationId;
    private String name;
    private BigDecimal availability;
    private Integer responseTimeMs;
    private Integer latencyP99Ms;
    private String supportLevel;
    private Integer incidentResponseMin;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}