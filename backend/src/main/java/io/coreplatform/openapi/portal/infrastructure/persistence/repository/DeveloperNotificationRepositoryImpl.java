package io.coreplatform.openapi.portal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.portal.application.domain.DeveloperNotification;
import io.coreplatform.openapi.portal.application.port.DeveloperNotificationRepository;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.DeveloperNotificationEntity;
import io.coreplatform.openapi.portal.infrastructure.persistence.mapper.DeveloperNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeveloperNotificationRepositoryImpl implements DeveloperNotificationRepository {

    private final DeveloperNotificationMapper mapper;

    @Override
    public DeveloperNotification save(DeveloperNotification notification) {
        DeveloperNotificationEntity entity = toEntity(notification);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<DeveloperNotification> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public IPage<DeveloperNotification> findByUserId(long page, long size, Long userId) {
        LambdaQueryWrapper<DeveloperNotificationEntity> wrapper = new LambdaQueryWrapper<DeveloperNotificationEntity>()
                .eq(DeveloperNotificationEntity::getUserId, userId)
                .orderByDesc(DeveloperNotificationEntity::getCreateTime);
        IPage<DeveloperNotificationEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public Long countUnreadByUserId(Long userId) {
        return mapper.selectCount(
                new LambdaQueryWrapper<DeveloperNotificationEntity>()
                        .eq(DeveloperNotificationEntity::getUserId, userId)
                        .eq(DeveloperNotificationEntity::getIsRead, 0)
        );
    }

    @Override
    public void markRead(Long id) {
        mapper.update(null,
                new LambdaUpdateWrapper<DeveloperNotificationEntity>()
                        .eq(DeveloperNotificationEntity::getId, id)
                        .set(DeveloperNotificationEntity::getIsRead, 1)
        );
    }

    @Override
    public void markAllRead(Long userId) {
        mapper.update(null,
                new LambdaUpdateWrapper<DeveloperNotificationEntity>()
                        .eq(DeveloperNotificationEntity::getUserId, userId)
                        .eq(DeveloperNotificationEntity::getIsRead, 0)
                        .set(DeveloperNotificationEntity::getIsRead, 1)
        );
    }

    private DeveloperNotificationEntity toEntity(DeveloperNotification domain) {
        DeveloperNotificationEntity entity = new DeveloperNotificationEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setType(domain.getType());
        entity.setIsRead(domain.getIsRead() != null && domain.getIsRead() ? 1 : 0);
        entity.setLink(domain.getLink());
        entity.setCreateTime(domain.getCreateTime());
        return entity;
    }

    private DeveloperNotification toDomain(DeveloperNotificationEntity entity) {
        return DeveloperNotification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .isRead(entity.getIsRead() != null && entity.getIsRead() == 1)
                .link(entity.getLink())
                .createTime(entity.getCreateTime())
                .build();
    }
}