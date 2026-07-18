package io.coreplatform.openapi.enterprise.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.coreplatform.openapi.enterprise.domain.Member;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.entity.MemberEntity;
import io.coreplatform.openapi.enterprise.infrastructure.persistence.mapper.MemberMapper;
import io.coreplatform.openapi.enterprise.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper mapper;

    @Override
    public Member save(Member member) {
        MemberEntity entity = toEntity(member);
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.updateById(entity);
        }
        return toDomain(entity);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<Member> findByOrgAndUser(Long organizationId, Long userId) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getOrganizationId, organizationId)
               .eq(MemberEntity::getUserId, userId);
        return Optional.ofNullable(mapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public IPage<Member> findPage(long page, long size, Long organizationId, Long teamId, String role) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        if (organizationId != null) {
            wrapper.eq(MemberEntity::getOrganizationId, organizationId);
        }
        if (teamId != null) {
            wrapper.eq(MemberEntity::getTeamId, teamId);
        }
        if (role != null && !role.isEmpty()) {
            wrapper.eq(MemberEntity::getRole, role);
        }
        wrapper.orderByDesc(MemberEntity::getCreateTime);
        return mapper.selectPage(new Page<>(page, size), wrapper).convert(this::toDomain);
    }

    @Override
    public List<Member> findByOrganizationId(Long organizationId) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getOrganizationId, organizationId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Member> findByTeamId(Long teamId) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getTeamId, teamId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Long countByOrgId(Long organizationId) {
        return mapper.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getOrganizationId, organizationId));
    }

    private MemberEntity toEntity(Member domain) {
        MemberEntity entity = new MemberEntity();
        entity.setId(domain.getId());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setTeamId(domain.getTeamId());
        entity.setUserId(domain.getUserId());
        entity.setRole(domain.getRole());
        entity.setStatus(domain.getStatus());
        entity.setJoinedAt(domain.getJoinedAt());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setCreateUser(domain.getCreateUser());
        entity.setUpdateUser(domain.getUpdateUser());
        return entity;
    }

    private Member toDomain(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId()).organizationId(entity.getOrganizationId())
                .teamId(entity.getTeamId()).userId(entity.getUserId())
                .role(entity.getRole()).status(entity.getStatus())
                .joinedAt(entity.getJoinedAt())
                .createTime(entity.getCreateTime()).updateTime(entity.getUpdateTime())
                .createUser(entity.getCreateUser()).updateUser(entity.getUpdateUser())
                .build();
    }
}