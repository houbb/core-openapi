package io.coreplatform.openapi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationToken {
    private String tokenType;
    private String principal;
    private Long applicationId;
    private Long userId;
    private String tenantId;
    private List<String> permissions;
}