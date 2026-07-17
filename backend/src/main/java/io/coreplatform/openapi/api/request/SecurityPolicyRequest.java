package io.coreplatform.openapi.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SecurityPolicyRequest {
    @NotNull(message = "API ID不能为空")
    private Long apiId;
    private String requiredPermission;
    private Integer authRequired;
    private String ipWhiteList;
    private Integer timeLimitEnabled;
    private String timeLimitStart;
    private String timeLimitEnd;
    private Integer tenantCheck;
}