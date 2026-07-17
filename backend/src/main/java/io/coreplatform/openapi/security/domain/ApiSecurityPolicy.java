package io.coreplatform.openapi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSecurityPolicy {
    private Long id;
    private Long apiId;
    private String requiredPermission;
    private Integer authRequired;
    private Integer signRequired;
    private String ipWhiteList;
    private Integer timeLimitEnabled;
    private String timeLimitStart;
    private String timeLimitEnd;
    private Integer tenantCheck;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}