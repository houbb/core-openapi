package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.IdentityProvider;
import io.coreplatform.openapi.enterprise.port.IdentityProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentityProviderService {

    private final IdentityProviderRepository identityProviderRepository;

    public IdentityProvider findById(Long id) {
        return identityProviderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("身份提供商不存在: " + id));
    }

    public List<IdentityProvider> findByOrganizationId(Long organizationId) {
        return identityProviderRepository.findByOrganizationId(organizationId);
    }

    @Transactional
    public IdentityProvider createProvider(Long organizationId, String providerType, String name, String configJson,
                                            Integer isDefault) {
        // If setting as default, unset existing default
        if (isDefault != null && isDefault == 1) {
            identityProviderRepository.findDefaultByOrgId(organizationId).ifPresent(p -> {
                p.setIsDefault(0);
                identityProviderRepository.save(p);
            });
        }

        IdentityProvider provider = IdentityProvider.builder()
                .organizationId(organizationId)
                .providerType(providerType)
                .name(name)
                .configJson(configJson)
                .isDefault(isDefault != null ? isDefault : 0)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return identityProviderRepository.save(provider);
    }

    @Transactional
    public IdentityProvider updateProvider(Long id, String name, String configJson, Integer isDefault, String status) {
        IdentityProvider provider = findById(id);
        if (name != null) provider.setName(name);
        if (configJson != null) provider.setConfigJson(configJson);
        if (status != null) provider.setStatus(status);
        if (isDefault != null) {
            if (isDefault == 1) {
                identityProviderRepository.findDefaultByOrgId(provider.getOrganizationId()).ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        p.setIsDefault(0);
                        identityProviderRepository.save(p);
                    }
                });
            }
            provider.setIsDefault(isDefault);
        }
        provider.setUpdateTime(LocalDateTime.now());
        return identityProviderRepository.save(provider);
    }

    @Transactional
    public void deleteProvider(Long id) {
        identityProviderRepository.deleteById(id);
    }
}