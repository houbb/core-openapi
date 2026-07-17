package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.request.TagMappingRequest;
import io.coreplatform.openapi.api.request.TagRequest;
import io.coreplatform.openapi.api.response.TagResponse;
import io.coreplatform.openapi.application.command.CreateTagCommand;
import io.coreplatform.openapi.application.domain.Tag;
import io.coreplatform.openapi.application.service.TagApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/openapi")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag Management", description = "API标签管理")
public class TagController {

    private final TagApplicationService tagApplicationService;

    @GetMapping("/tags")
    @Operation(summary = "获取标签列表")
    public PageResult<TagResponse> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") long size) {
        IPage<io.coreplatform.openapi.application.domain.Tag> result = tagApplicationService.findPage(page, size);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @PostMapping("/tags")
    @Operation(summary = "创建标签")
    public TagResponse create(@Valid @RequestBody TagRequest request) {
        CreateTagCommand command = new CreateTagCommand();
        command.setName(request.getName());
        command.setColor(request.getColor());

        io.coreplatform.openapi.application.domain.Tag tag = tagApplicationService.createTag(command);
        return toResponse(tag);
    }

    @PutMapping("/tags/{id}")
    @Operation(summary = "更新标签")
    public TagResponse update(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        CreateTagCommand command = new CreateTagCommand();
        command.setName(request.getName());
        command.setColor(request.getColor());

        io.coreplatform.openapi.application.domain.Tag tag = tagApplicationService.updateTag(id, command);
        return toResponse(tag);
    }

    @DeleteMapping("/tags/{id}")
    @Operation(summary = "删除标签")
    public void delete(@PathVariable Long id) {
        tagApplicationService.deleteTag(id);
    }

    // ---- API 标签关联 ----

    @PutMapping("/definitions/{apiId}/tags")
    @Operation(summary = "设置接口标签")
    public void setTags(@PathVariable Long apiId, @RequestBody TagMappingRequest request) {
        tagApplicationService.setDefinitionTags(apiId, request.getTagIds());
    }

    @GetMapping("/definitions/{apiId}/tags")
    @Operation(summary = "获取接口标签")
    public List<TagResponse> getTags(@PathVariable Long apiId) {
        return tagApplicationService.getDefinitionTags(apiId).stream()
                .map(this::toResponse).toList();
    }

    private TagResponse toResponse(io.coreplatform.openapi.application.domain.Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .createTime(tag.getCreateTime())
                .updateTime(tag.getUpdateTime())
                .build();
    }
}
