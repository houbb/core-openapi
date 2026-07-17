package io.coreplatform.openapi.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiVersion {
    private Long id;
    private Long apiId;
    private String version;
    private String status;
    private String changelog;
    private LocalDateTime releaseTime;
    private LocalDateTime deprecatedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
