package io.coreplatform.openapi.marketplace.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceItemResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String iconUrl;
    private String providerName;
    private String providerType;
    private Boolean verified;
    private Double avgRating;
    private Long reviewCount;
}