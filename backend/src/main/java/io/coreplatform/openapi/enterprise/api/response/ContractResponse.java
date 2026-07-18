package io.coreplatform.openapi.enterprise.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private Long id;
    private Long organizationId;
    private String contractNo;
    private String name;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long maxRequests;
    private Integer maxQps;
    private Integer supportsPhone;
    private Integer supports724;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}