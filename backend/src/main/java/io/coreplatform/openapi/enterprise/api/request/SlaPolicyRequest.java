package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SlaPolicyRequest {
    @NotBlank(message = "SLA名称不能为空")
    private String name;

    @DecimalMin(value = "0.0000", message = "可用性不能小于0")
    @DecimalMax(value = "1.0000", message = "可用性不能大于1")
    private BigDecimal availability;

    private Integer responseTimeMs;

    private Integer latencyP99Ms;

    private String supportLevel;

    private Integer incidentResponseMin;
}