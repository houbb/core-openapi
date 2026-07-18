package io.coreplatform.openapi.marketplace.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.marketplace.domain.ApiProvider;
import io.coreplatform.openapi.marketplace.port.ApiProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiProviderService {

    private final ApiProviderRepository providerRepository;

    public IPage<ApiProvider> findPage(long page, long size, String keyword, String type, String status) {
        return providerRepository.findPage(page, size, keyword, type, status);
    }

    public ApiProvider findById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider 不存在: " + id));
    }

    public List<ApiProvider> findAll() {
        return providerRepository.findAll();
    }

    @Transactional
    public ApiProvider createProvider(String name, String description, String type, Long ownerId,
                                       String contactEmail, String website) {
        ApiProvider provider = ApiProvider.builder()
                .name(name)
                .description(description)
                .type(type != null ? type : "COMMUNITY")
                .ownerId(ownerId)
                .verified(0)
                .status("ACTIVE")
                .contactEmail(contactEmail)
                .website(website)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return providerRepository.save(provider);
    }

    @Transactional
    public ApiProvider updateProvider(Long id, String name, String description, String contactEmail,
                                       String website, String logoUrl) {
        ApiProvider provider = findById(id);
        if (name != null) provider.setName(name);
        if (description != null) provider.setDescription(description);
        if (contactEmail != null) provider.setContactEmail(contactEmail);
        if (website != null) provider.setWebsite(website);
        if (logoUrl != null) provider.setLogoUrl(logoUrl);
        provider.setUpdateTime(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    @Transactional
    public ApiProvider verifyProvider(Long id) {
        ApiProvider provider = findById(id);
        provider.setVerified(1);
        provider.setUpdateTime(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    @Transactional
    public ApiProvider toggleStatus(Long id, String status) {
        ApiProvider provider = findById(id);
        provider.setStatus(status);
        provider.setUpdateTime(LocalDateTime.now());
        return providerRepository.save(provider);
    }

    @Transactional
    public void deleteProvider(Long id) {
        providerRepository.deleteById(id);
    }

    public Long countByStatus(String status) {
        return providerRepository.countByStatus(status);
    }

    public Long countAll() {
        return providerRepository.countAll();
    }
}