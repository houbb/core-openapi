package io.coreplatform.openapi.portal.api.request;

import lombok.Data;

@Data
public class DeveloperUpdateRequest {
    private String email;
    private String avatarUrl;
    private String companyName;
    private String phone;
}