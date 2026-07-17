package io.coreplatform.openapi.portal.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotNull(message = "API ID不能为空")
    private Long apiId;
    private String plan;
}