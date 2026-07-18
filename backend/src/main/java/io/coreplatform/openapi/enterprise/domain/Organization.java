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
public class Organization {
    private Long id;
    private String name;
    private String code;
    private String type;
    private Long ownerId;
    private String status;
    private String description;
    private String logoUrl;
    private String website;
    private String contactEmail;
    private String contactPhone;
    private String tenantId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
