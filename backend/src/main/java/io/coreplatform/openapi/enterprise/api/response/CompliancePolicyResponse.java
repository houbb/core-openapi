package io.coreplatform.openapi.enterprise.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompliancePolicyResponse {
    private Long id;
    private Long organizationId;
    private String name;
    private String policyType;
    private String configJson;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}