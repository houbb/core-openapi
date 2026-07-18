package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.coreplatform.openapi.enterprise.domain.Team;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.TeamEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.TeamMapper;
import io.coreplatform.openapi.enterprise.port.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamMapper mapper;

    @Override
    public Team save(Team team) {
        TeamEntity entity = toEntity(team);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Team> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<TeamEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamEntity::getOrganizationId, organizationId).orderByAsc(TeamEntity::getSortOrder);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Team> findByParentId(Long parentId) {
        LambdaQueryWrapper<TeamEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamEntity::getParentId, parentId).orderByAsc(TeamEntity::getSortOrder);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByOrgId(Long organizationId) {
        return mapper.selectCount(new LambdaQueryWrapper<TeamEntity>().eq(TeamEntity::getOrganizationId, organizationId));
    }

    private TeamEntity toEntity(Team domain) {
        TeamEntity entity = new TeamEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setParentId(domain.getParentId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setLeaderId(domain.getLeaderId());
        entity.setStatus(domain.getStatus());
        entity.setSortOrder(domain.getSortOrder());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Team toDomain(TeamEntity entity) {
        return Team.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .parentId(entity.getParentId()).name(entity.getName())
                .description(entity.getDescription()).leaderId(entity.getLeaderId())
                .status(entity.getStatus()).sortOrder(entity.getSortOrder())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}