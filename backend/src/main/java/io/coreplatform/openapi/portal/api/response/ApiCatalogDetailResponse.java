package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCatalogDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String method;
    private String path;
    private String version;
    private String serviceName;
    private String category;
    private String status;
    private List<ParameterInfo> parameters;
    private List<ResponseInfo> responses;
    private String requestSchema;
    private List<String> examples;
    private boolean subscribed;
    private String sdkAvailability;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterInfo {
        private String name;
        private String location;
        private String type;
        private boolean required;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseInfo {
        private String statusCode;
        private String description;
        private String schema;
    }
}