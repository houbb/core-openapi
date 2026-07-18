package io.coreplatform.openapi.enterprise.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Partner;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository {
    Partner save(Partner partner);
    Optional<Partner> findById(Long id);
    IPage<Partner> findPage(long page, long size, String keyword, String level, String status, Long organizationId);
    List<Partner> findByOrganizationId(Long organizationId);
    void deleteById(Long id);
    Long countByLevel(String level);
    Long countAll();
}
