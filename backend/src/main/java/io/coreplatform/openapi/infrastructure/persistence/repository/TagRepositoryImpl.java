package io.coreplatform.openapi.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.application.domain.Tag;
import io.coreplatform.openapi.application.port.TagRepository;
import io.coreplatform.openapi.infrastructure.persistence.entity.TagEntity;
import io.coreplatform.openapi.infrastructure.persistence.entity.TagMappingEntity;
import io.coreplatform.openapi.infrastructure.persistence.mapper.TagMapper;
import io.coreplatform.openapi.infrastructure.persistence.mapper.TagMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;
    private final TagMappingMapper tagMappingMapper;

    @Override
    public Tag save(Tag tag) {
        TagEntity entity = toEntity(tag);
        if (entity.getId() == null) {
            tagMapper.insert(entity);
        } else {
            tagMapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Tag> findById(Long id) {
        TagEntity entity = tagMapper.selectById(id);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    @Override
    public IPage<Tag> findPage(long page, long size) {
        IPage<TagEntity> entityPage = tagMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<TagEntity>().orderByDesc(TagEntity::getUpdateTime)
        );
        return entityPage.convert(this::toDomain);
    }

    @Override
    public List<Tag> findAll() {
        List<TagEntity> entities = tagMapper.selectList(null);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        tagMapper.deleteById(id);
        // Also delete mappings
        tagMappingMapper.delete(new LambdaQueryWrapper<TagMappingEntity>()
                .eq(TagMappingEntity::getTagId, id));
    }

    @Override
    public void saveMapping(Long apiId, List<Long> tagIds) {
        List<TagMappingEntity> mappings = new ArrayList<>();
        for (Long tagId : tagIds) {
            TagMappingEntity mapping = new TagMappingEntity();
            mapping.setTagId(tagId);
            mapping.setApiId(apiId);
            mappings.add(mapping);
        }
        tagMappingMapper.insert(mappings);
    }

    @Override
    public void deleteMappingsByApiId(Long apiId) {
        tagMappingMapper.delete(new LambdaQueryWrapper<TagMappingEntity>()
                .eq(TagMappingEntity::getApiId, apiId));
    }

    @Override
    public List<Tag> findTagsByApiId(Long apiId) {
        List<TagMappingEntity> mappings = tagMappingMapper.selectList(
                new LambdaQueryWrapper<TagMappingEntity>()
                        .eq(TagMappingEntity::getApiId, apiId)
        );
        if (mappings.isEmpty()) {
            return List.of();
        }
        List<Long> tagIds = mappings.stream().map(TagMappingEntity::getTagId).collect(Collectors.toList());
        List<TagEntity> entities = tagMapper.selectBatchIds(tagIds);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    // ---- Entity <-> Domain conversion ----

    private TagEntity toEntity(Tag domain) {
        TagEntity entity = new TagEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setColor(domain.getColor());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private Tag toDomain(TagEntity entity) {
        return Tag.builder()
                .id(entity.getId())
                .name(entity.getName())
                .color(entity.getColor())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
