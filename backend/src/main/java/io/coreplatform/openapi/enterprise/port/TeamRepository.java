package io.coreplatform.openapi.enterprise.port;

import io.coreplatform.openapi.enterprise.domain.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(Long id);
    List<Team> findByOrganizationId(Long organizationId);
    List<Team> findByParentId(Long parentId);
    void deleteById(Long id);
    Long countByOrgId(Long organizationId);
}
