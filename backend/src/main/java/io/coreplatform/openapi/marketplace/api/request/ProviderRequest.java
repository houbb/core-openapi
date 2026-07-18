package io.coreplatform.openapi.marketplace.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderRequest {
    @NotBlank(message = "Provider 名称不能为空")
    private String name;
    private String description;
    private String type;
    private Long ownerId;
    private String contactEmail;
    private String website;
    private String logoUrl;
}