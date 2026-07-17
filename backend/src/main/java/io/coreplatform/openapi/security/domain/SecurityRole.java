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
public class SecurityRole {
    private Long id;
    private String name;
    private String description;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}