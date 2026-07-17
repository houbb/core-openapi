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
public class Application {
    private Long id;
    private String appName;
    private String appCode;
    private Long ownerId;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}