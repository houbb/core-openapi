package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamRequest {
    @NotBlank(message = "团队名称不能为空")
    private String name;

    private String description;

    private Long parentId;

    private Long leaderId;
}