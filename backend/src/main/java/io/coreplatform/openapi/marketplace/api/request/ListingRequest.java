package io.coreplatform.openapi.marketplace.api.request;

import lombok.Data;

@Data
public class ListingRequest {
    private Integer featured;
    private Integer sortOrder;
    private String tags;
    private String highlightText;
}