package io.coreplatform.openapi.portal.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.portal.application.domain.PortalDocument;

import java.util.List;
import java.util.Optional;

public interface PortalDocumentRepository {
    PortalDocument save(PortalDocument doc);
    Optional<PortalDocument> findById(Long id);
    Optional<PortalDocument> findBySlug(String slug);
    List<PortalDocument> findByCategory(String category);
    IPage<PortalDocument> findPage(long page, long size, String keyword);
    void deleteById(Long id);
}
