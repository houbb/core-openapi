package io.coreplatform.openapi.enterprise.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Organization;
import io.coreplatform.openapi.enterprise.port.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public IPage<Organization> findPage(long page, long size, String keyword, String type, String status) {
        return organizationRepository.findPage(page, size, keyword, type, status);
    }

    public Organization findById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("组织不存在: " + id));
    }

    @Transactional
    public Organization createOrganization(String name, String type, Long ownerId, String description,
                                            String contactEmail, String contactPhone, String website) {
        Organization org = Organization.builder()
                .name(name)
                .code("ORG_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .type(type != null ? type : "ENTERPRISE")
                .ownerId(ownerId)
                .status("ACTIVE")
                .description(description)
                .contactEmail(contactEmail)
                .contactPhone(contactPhone)
                .website(website)
                .tenantId("tenant_" + System.currentTimeMillis())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return organizationRepository.save(org);
    }

    @Transactional
    public Organization updateOrganization(Long id, String name, String description, String contactEmail,
                                            String contactPhone, String website, String logoUrl) {
        Organization org = findById(id);
        if (name != null) org.setName(name);
        if (description != null) org.setDescription(description);
        if (contactEmail != null) org.setContactEmail(contactEmail);
        if (contactPhone != null) org.setContactPhone(contactPhone);
        if (website != null) org.setWebsite(website);
        if (logoUrl != null) org.setLogoUrl(logoUrl);
        org.setUpdateTime(LocalDateTime.now());
        return organizationRepository.save(org);
    }

    @Transactional
    public Organization updateStatus(Long id, String status) {
        Organization org = findById(id);
        org.setStatus(status);
        org.setUpdateTime(LocalDateTime.now());
        return organizationRepository.save(org);
    }

    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public List<Organization> findByTenantId(String tenantId) {
        return organizationRepository.findByTenantId(tenantId);
    }

    public Long countByStatus(String status) {
        return organizationRepository.countByStatus(status);
    }

    public Long countAll() {
        return organizationRepository.countAll();
    }
}