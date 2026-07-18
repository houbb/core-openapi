package io.coreplatform.openapi.enterprise.application.service;

import io.coreplatform.openapi.enterprise.domain.Team;
import io.coreplatform.openapi.enterprise.port.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public Team findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在: " + id));
    }

    public List<Team> findByOrganizationId(Long organizationId) {
        return teamRepository.findByOrganizationId(organizationId);
    }

    public List<Team> findByParentId(Long parentId) {
        return teamRepository.findByParentId(parentId);
    }

    @Transactional
    public Team createTeam(Long organizationId, Long parentId, String name, String description, Long leaderId) {
        Team team = Team.builder()
                .organizationId(organizationId)
                .parentId(parentId)
                .name(name)
                .description(description)
                .leaderId(leaderId)
                .status("ACTIVE")
                .sortOrder(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Long id, String name, String description, Long leaderId, Long parentId) {
        Team team = findById(id);
        if (name != null) team.setName(name);
        if (description != null) team.setDescription(description);
        if (leaderId != null) team.setLeaderId(leaderId);
        if (parentId != null) team.setParentId(parentId);
        team.setUpdateTime(LocalDateTime.now());
        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public Long countByOrgId(Long organizationId) {
        return teamRepository.countByOrgId(organizationId);
    }
}