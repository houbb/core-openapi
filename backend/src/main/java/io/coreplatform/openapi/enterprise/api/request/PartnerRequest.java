package io.coreplatform.openapi.enterprise.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PartnerRequest {
    @NotBlank(message = "合作伙伴名称不能为空")
    private String name;

    private String level;

    private String contactName;

    private String contactEmail;

    private String contactPhone;

    private String description;

    private Long organizationId;
}