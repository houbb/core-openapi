package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MemberRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long teamId;

    private String role;
}