package io.coreplatform.openapi.enterprise.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnterpriseDashboardService {

    private final OrganizationService organizationService;
    private final MemberService memberService;
    private final PartnerService partnerService;
    private final ContractService contractService;

    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalOrganizations", organizationService.countAll());
        dashboard.put("activeOrganizations", organizationService.countByStatus("ACTIVE"));
        dashboard.put("totalPartners", partnerService.countAll());
        dashboard.put("strategicPartners", partnerService.countByLevel("STRATEGIC"));
        dashboard.put("activeContracts", contractService.countByStatus("ACTIVE"));
        dashboard.put("draftContracts", contractService.countByStatus("DRAFT"));
        return dashboard;
    }

    public Map<String, Object> getOrganizationDashboard(Long organizationId) {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("organizationId", organizationId);
        dashboard.put("memberCount", memberService.countByOrgId(organizationId));
        dashboard.put("partnerCount", partnerService.findByOrganizationId(organizationId).size());
        dashboard.put("contractCount", contractService.findByOrganizationId(organizationId).size());
        return dashboard;
    }
}