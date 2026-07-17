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
public class DeveloperResponse {
    private Long id;
    private String username;
    private String email;
    private String developerType;
    private String avatarUrl;
    private String companyName;
    private String phone;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}