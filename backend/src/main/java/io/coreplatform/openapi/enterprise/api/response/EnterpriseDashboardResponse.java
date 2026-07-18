package io.coreplatform.openapi.enterprise.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseDashboardResponse {
    private Long totalOrganizations;
    private Long activeOrganizations;
    private Long totalPartners;
    private Long strategicPartners;
    private Long activeContracts;
    private Long draftContracts;
    private Map<String, Object> details;
}