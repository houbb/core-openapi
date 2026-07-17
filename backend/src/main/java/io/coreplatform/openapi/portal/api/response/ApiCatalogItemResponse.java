package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCatalogItemResponse {
    private Long id;
    private String name;
    private String description;
    private String method;
    private String path;
    private String version;
    private String serviceName;
    private String category;
    private String status;
}