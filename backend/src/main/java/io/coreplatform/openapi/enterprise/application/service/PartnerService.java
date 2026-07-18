package io.coreplatform.openapi.enterprise.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Partner;
import io.coreplatform.openapi.enterprise.port.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    public IPage<Partner> findPage(long page, long size, String keyword, String level, String status, Long organizationId) {
        return partnerRepository.findPage(page, size, keyword, level, status, organizationId);
    }

    public Partner findById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("合作伙伴不存在: " + id));
    }

    @Transactional
    public Partner createPartner(Long organizationId, String name, String level, String contactName,
                                  String contactEmail, String contactPhone, String description) {
        Partner partner = Partner.builder()
                .organizationId(organizationId)
                .name(name)
                .level(level != null ? level : "STANDARD")
                .status("ACTIVE")
                .contactName(contactName)
                .contactEmail(contactEmail)
                .contactPhone(contactPhone)
                .description(description)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return partnerRepository.save(partner);
    }

    @Transactional
    public Partner updatePartner(Long id, String name, String level, String contactName,
                                  String contactEmail, String contactPhone, String description) {
        Partner partner = findById(id);
        if (name != null) partner.setName(name);
        if (level != null) partner.setLevel(level);
        if (contactName != null) partner.setContactName(contactName);
        if (contactEmail != null) partner.setContactEmail(contactEmail);
        if (contactPhone != null) partner.setContactPhone(contactPhone);
        if (description != null) partner.setDescription(description);
        partner.setUpdateTime(LocalDateTime.now());
        return partnerRepository.save(partner);
    }

    @Transactional
    public Partner updateStatus(Long id, String status) {
        Partner partner = findById(id);
        partner.setStatus(status);
        partner.setUpdateTime(LocalDateTime.now());
        return partnerRepository.save(partner);
    }

    @Transactional
    public void deletePartner(Long id) {
        partnerRepository.deleteById(id);
    }

    public List<Partner> findByOrganizationId(Long organizationId) {
        return partnerRepository.findByOrganizationId(organizationId);
    }

    public Long countByLevel(String level) {
        return partnerRepository.countByLevel(level);
    }

    public Long countAll() {
        return partnerRepository.countAll();
    }
}