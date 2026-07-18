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
public class IdentityProvider {
    private Long id;
    private Long organizationId;
    private String providerType;
    private String name;
    private String configJson;
    private Integer isDefault;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createUser;
    private String updateUser;
}
