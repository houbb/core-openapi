package io.coreplatform.openapi.portal.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.portal.application.domain.DeveloperSetting;
import io.coreplatform.openapi.portal.application.port.DeveloperSettingRepository;
import io.coreplatform.openapi.portal.infrastructure.persistence.entity.DeveloperSettingEntity;
import io.coreplatform.openapi.portal.infrastructure.persistence.mapper.DeveloperSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DeveloperSettingRepositoryImpl implements DeveloperSettingRepository {

    private final DeveloperSettingMapper mapper;

    @Override
    public DeveloperSetting save(DeveloperSetting setting) {
        DeveloperSettingEntity entity = toEntity(setting);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<DeveloperSetting> findByUserId(Long userId) {
        DeveloperSettingEntity entity = mapper.selectOne(
                new LambdaQueryWrapper<DeveloperSettingEntity>()
                        .eq(DeveloperSettingEntity::getUserId, userId)
        );
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    private DeveloperSettingEntity toEntity(DeveloperSetting domain) {
        DeveloperSettingEntity entity = new DeveloperSettingEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setLanguage(domain.getLanguage());
        entity.setTheme(domain.getTheme());
        entity.setNotifyEmail(domain.getNotifyEmail() != null && domain.getNotifyEmail() ? 1 : 0);
        entity.setNotifyUsage(domain.getNotifyUsage() != null && domain.getNotifyUsage() ? 1 : 0);
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        return entity;
    }

    private DeveloperSetting toDomain(DeveloperSettingEntity entity) {
        return DeveloperSetting.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .language(entity.getLanguage())
                .theme(entity.getTheme())
                .notifyEmail(entity.getNotifyEmail() != null && entity.getNotifyEmail() == 1)
                .notifyUsage(entity.getNotifyUsage() != null && entity.getNotifyUsage() == 1)
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}