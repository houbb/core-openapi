package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequest {
    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    @NotBlank(message = "合同名称不能为空")
    private String name;

    private String planName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long maxRequests;

    private Integer maxQps;

    private Integer supportsPhone;

    private Integer supports724;

    private String description;
}