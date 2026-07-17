package io.coreplatform.openapi.portal.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperAppResponse {
    private Long id;
    private String appName;
    private String appCode;
    private String description;
    private String status;
    private int keyCount;
    private int subscriptionCount;
    private LocalDateTime createTime;
}