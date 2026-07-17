package io.coreplatform.openapi.portal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.portal.application.domain.PortalDocument;
import io.coreplatform.openapi.portal.application.port.PortalDocumentRepository;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.PortalDocumentEntity;
import io.coreplatform.openapi.portal.infrastructure.persistence.mapper.PortalDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PortalDocumentRepositoryImpl implements PortalDocumentRepository {

    private final PortalDocumentMapper mapper;

    @Override
    public PortalDocument save(PortalDocument doc) {
        PortalDocumentEntity entity = toEntity(doc);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<PortalDocument> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<PortalDocument> findBySlug(String slug) {
        PortalDocumentEntity entity = mapper.selectOne(
                new LambdaQueryWrapper<PortalDocumentEntity>()
                        .eq(PortalDocumentEntity::getSlug, slug)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public List<PortalDocument> findByCategory(String category) {
        return mapper.selectList(
                new LambdaQueryWrapper<PortalDocumentEntity>()
                        .eq(PortalDocumentEntity::getCategory, category)
                        .eq(PortalDocumentEntity::getStatus, "PUBLISHED")
                        .orderByAsc(PortalDocumentEntity::getSortOrder)
        ).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public IPage<PortalDocument> findPage(long page, long size, String keyword) {
        LambdaQueryWrapper<PortalDocumentEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PortalDocumentEntity::getTitle, keyword)
                    .or()
                    .like(PortalDocumentEntity::getContent, keyword);
        }
        wrapper.orderByAsc(PortalDocumentEntity::getSortOrder);
        IPage<PortalDocumentEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    private PortalDocumentEntity toEntity(PortalDocument domain) {
        PortalDocumentEntity entity = new PortalDocumentEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setSlug(domain.getSlug());
        entity.setCategory(domain.getCategory());
        entity.setContent(domain.getContent());
        entity.setSortOrder(domain.getSortOrder());
        entity.setStatus(domain.getStatus());
        entity.setAuthor(domain.getAuthor());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private PortalDocument toDomain(PortalDocumentEntity entity) {
        return PortalDocument.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .category(entity.getCategory())
                .content(entity.getContent())
                .sortOrder(entity.getSortOrder())
                .status(entity.getStatus())
                .author(entity.getAuthor())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}