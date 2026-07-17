package io.coreplatform.openapi.portal.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperSetting {
    private Long id;
    private Long userId;
    private String language;
    private String theme;
    private Boolean notifyEmail;
    private Boolean notifyUsage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}