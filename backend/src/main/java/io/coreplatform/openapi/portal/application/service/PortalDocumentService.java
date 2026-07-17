package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.response.DocumentResponse;
import io.coreplatform.openapi.portal.application.domain.PortalDocument;
import io.coreplatform.openapi.portal.application.port.PortalDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortalDocumentService {

    private final PortalDocumentRepository documentRepository;

    public List<DocumentResponse> listByCategory(String category) {
        return documentRepository.findByCategory(category).stream()
                .filter(d -> "PUBLISHED".equals(d.getStatus()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DocumentResponse getBySlug(String slug) {
        PortalDocument doc = documentRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("文档不存在: " + slug));
        return toResponse(doc);
    }

    public List<DocumentResponse> listAll() {
        return documentRepository.findPage(1, 100, null).getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponse create(PortalDocument doc) {
        if (doc.getCreateTime() == null) doc.setCreateTime(LocalDateTime.now());
        if (doc.getUpdateTime() == null) doc.setUpdateTime(LocalDateTime.now());
        PortalDocument saved = documentRepository.save(doc);
        return toResponse(saved);
    }

    private DocumentResponse toResponse(PortalDocument doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .slug(doc.getSlug())
                .category(doc.getCategory())
                .content(doc.getContent())
                .sortOrder(doc.getSortOrder())
                .status(doc.getStatus())
                .author(doc.getAuthor())
                .createTime(doc.getCreateTime())
                .updateTime(doc.getUpdateTime())
                .build();
    }
}