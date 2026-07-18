package io.coreplatform.openapi.enterprise.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner {
    private Long id;
    private Long organizationId;
    private String name;
    private String level;
    private String status;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String description;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
