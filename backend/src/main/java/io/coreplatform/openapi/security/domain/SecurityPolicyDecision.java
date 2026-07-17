package io.coreplatform.openapi.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPolicyDecision {
    private boolean allowed;
    private String reason;
    private String requiredPermission;
}