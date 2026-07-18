package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequest {
    @NotBlank(message = "组织名称不能为空")
    private String name;

    private String type;

    private Long ownerId;

    private String description;

    private String contactEmail;

    private String contactPhone;

    private String website;

    private String logoUrl;
}