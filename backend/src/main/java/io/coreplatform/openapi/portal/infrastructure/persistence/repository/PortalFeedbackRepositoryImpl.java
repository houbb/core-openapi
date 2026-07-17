package io.coreplatform.openapi.portal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.portal.application.domain.PortalFeedback;
import io.coreplatform.openapi.portal.application.port.PortalFeedbackRepository;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.PortalFeedbackEntity;
import io.coreplatform.openapi.portal.infrastructure.persistence.mapper.PortalFeedbackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PortalFeedbackRepositoryImpl implements PortalFeedbackRepository {

    private final PortalFeedbackMapper mapper;

    @Override
    public PortalFeedback save(PortalFeedback feedback) {
        PortalFeedbackEntity entity = toEntity(feedback);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<PortalFeedback> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public IPage<PortalFeedback> findByUserId(long page, long size, Long userId) {
        LambdaQueryWrapper<PortalFeedbackEntity> wrapper = new LambdaQueryWrapper<PortalFeedbackEntity>()
                .eq(PortalFeedbackEntity::getUserId, userId)
                .orderByDesc(PortalFeedbackEntity::getCreateTime);
        IPage<PortalFeedbackEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    @Override
    public IPage<PortalFeedback> findPage(long page, long size, String status) {
        LambdaQueryWrapper<PortalFeedbackEntity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PortalFeedbackEntity::getStatus, status);
        }
        wrapper.orderByDesc(PortalFeedbackEntity::getCreateTime);
        IPage<PortalFeedbackEntity> entityPage = mapper.selectPage(new Page<>(page, size), wrapper);
        return entityPage.convert(this::toDomain);
    }

    private PortalFeedbackEntity toEntity(PortalFeedback domain) {
        PortalFeedbackEntity entity = new PortalFeedbackEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setType(domain.getType());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setStatus(domain.getStatus());
        entity.setAdminReply(domain.getAdminReply());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private PortalFeedback toDomain(PortalFeedbackEntity entity) {
        return PortalFeedback.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus())
                .adminReply(entity.getAdminReply())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}