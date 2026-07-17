package io.coreplatform.openapi.api.controller;

import io.coreplatform.openapi.api.request.VersionRequest;
import io.coreplatform.openapi.api.response.VersionResponse;
import io.coreplatform.openapi.application.command.CreateVersionCommand;
import io.coreplatform.openapi.application.domain.ApiVersion;
import io.coreplatform.openapi.application.service.VersionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi/definitions/{apiId}/versions")
@RequiredArgsConstructor
@Tag(name = "Version Management", description = "API版本管理")
public class VersionController {

    private final VersionApplicationService versionApplicationService;

    @GetMapping
    @Operation(summary = "获取接口版本列表")
    public List<VersionResponse> list(@PathVariable Long apiId) {
        return versionApplicationService.findByApiId(apiId).stream()
                .map(this::toResponse).toList();
    }

    @PostMapping
    @Operation(summary = "创建新版本")
    public VersionResponse create(@PathVariable Long apiId, @Valid @RequestBody VersionRequest request) {
        CreateVersionCommand command = new CreateVersionCommand();
        command.setApiId(apiId);
        command.setVersion(request.getVersion());
        command.setChangelog(request.getChangelog());

        ApiVersion version = versionApplicationService.createVersion(command);
        return toResponse(version);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新版本信息")
    public VersionResponse update(@PathVariable Long apiId, @PathVariable Long id, @Valid @RequestBody VersionRequest request) {
        CreateVersionCommand command = new CreateVersionCommand();
        command.setApiId(apiId);
        command.setVersion(request.getVersion());
        command.setChangelog(request.getChangelog());

        ApiVersion version = versionApplicationService.updateVersion(id, command);
        return toResponse(version);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "激活版本")
    public VersionResponse activate(@PathVariable Long apiId, @PathVariable Long id) {
        return toResponse(versionApplicationService.activate(id));
    }

    @PostMapping("/{id}/deprecate")
    @Operation(summary = "废弃版本")
    public VersionResponse deprecate(@PathVariable Long apiId, @PathVariable Long id) {
        return toResponse(versionApplicationService.deprecate(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除版本")
    public void delete(@PathVariable Long apiId, @PathVariable Long id) {
        versionApplicationService.deleteVersion(id);
    }

    private VersionResponse toResponse(ApiVersion version) {
        return VersionResponse.builder()
                .id(version.getId())
                .apiId(version.getApiId())
                .version(version.getVersion())
                .status(version.getStatus())
                .changelog(version.getChangelog())
                .releaseTime(version.getReleaseTime())
                .deprecatedTime(version.getDeprecatedTime())
                .createTime(version.getCreateTime())
                .updateTime(version.getUpdateTime())
                .build();
    }
}