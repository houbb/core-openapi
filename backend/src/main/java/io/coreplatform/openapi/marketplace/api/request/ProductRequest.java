package io.coreplatform.openapi.marketplace.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;
    private String description;
    private Long providerId;
    private Long apiId;
    private String category;
    private String iconUrl;
}