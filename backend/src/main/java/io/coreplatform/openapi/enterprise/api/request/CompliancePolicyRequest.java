package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompliancePolicyRequest {
    @NotBlank(message = "策略名称不能为空")
    private String name;

    @NotBlank(message = "策略类型不能为空")
    private String policyType;

    private String configJson;

    private String status;
}