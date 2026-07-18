package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.enterprise.api.request.TeamRequest;
import io.coreplatform.openapi.enterprise.api.response.TeamResponse;
import io.coreplatform.openapi.enterprise.application.service.TeamService;
import io.coreplatform.openapi.enterprise.domain.Team;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/organizations/{orgId}/teams")
@RequiredArgsConstructor
@Tag(name = "Enterprise Team", description = "企业团队管理")
public class EnterpriseTeamController {

    private final TeamService teamService;

    @GetMapping
    @Operation(summary = "获取组织下的团队列表")
    public List<TeamResponse> list(@PathVariable Long orgId) {
        return teamService.findByOrganizationId(orgId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取团队详情")
    public TeamResponse get(@PathVariable Long orgId, @PathVariable Long id) {
        return toResponse(teamService.findById(id));
    }

    @PostMapping
    @Operation(summary = "创建团队")
    public TeamResponse create(@PathVariable Long orgId, @Valid @RequestBody TeamRequest request) {
        Team team = teamService.createTeam(orgId, request.getParentId(), request.getName(),
                request.getDescription(), request.getLeaderId());
        return toResponse(team);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新团队")
    public TeamResponse update(@PathVariable Long orgId, @PathVariable Long id, @Valid @RequestBody TeamRequest request) {
        Team team = teamService.updateTeam(id, request.getName(), request.getDescription(),
                request.getLeaderId(), request.getParentId());
        return toResponse(team);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除团队")
    public void delete(@PathVariable Long orgId, @PathVariable Long id) {
        teamService.deleteTeam(id);
    }

    private TeamResponse toResponse(Team t) {
        return TeamResponse.builder()
                .id(t.getId()).organizationId(t.getOrganizationId())
                .parentId(t.getParentId()).name(t.getName())
                .description(t.getDescription()).leaderId(t.getLeaderId())
                .status(t.getStatus()).sortOrder(t.getSortOrder())
                .createTime(t.getCreateTime()).updateTime(t.getUpdateTime())
                .build();
    }
}