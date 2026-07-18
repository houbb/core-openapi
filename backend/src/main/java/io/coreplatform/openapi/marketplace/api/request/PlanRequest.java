package io.coreplatform.openapi.marketplace.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PlanRequest {
    @NotBlank(message = "计划名称不能为空")
    private String name;
    private String description;
    private BigDecimal price;
    private String billingType;
    private String limitConfig;
    private Integer sortOrder;
}