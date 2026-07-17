package io.coreplatform.openapi.portal.api.request;

import lombok.Data;

@Data
public class SettingUpdateRequest {
    private String language;
    private String theme;
    private Boolean notifyEmail;
    private Boolean notifyUsage;
}