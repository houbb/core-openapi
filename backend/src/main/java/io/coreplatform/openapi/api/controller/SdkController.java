package io.coreplatform.openapi.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.api.exception.ResourceNotFoundException;
import io.coreplatform.openapi.api.request.SdkGenerateRequest;
import io.coreplatform.openapi.api.response.SdkGenerationResponse;
import io.coreplatform.openapi.api.response.SdkProjectResponse;
import io.coreplatform.openapi.application.port.SdkGenerationRepository;
import io.coreplatform.openapi.application.port.SdkProjectRepository;
import io.coreplatform.openapi.sdk.domain.SdkGeneration;
import io.coreplatform.openapi.sdk.domain.SdkProject;
import io.coreplatform.openapi.sdk.service.SdkGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/openapi/sdk")
@RequiredArgsConstructor
@Tag(name = "SDK Management", description = "SDK生成管理")
public class SdkController {

    private final SdkGeneratorService generatorService;
    private final SdkProjectRepository projectRepository;
    private final SdkGenerationRepository generationRepository;

    @PostMapping("/generate")
    @Operation(summary = "生成 SDK")
    public SdkGenerationResponse generate(@Valid @RequestBody SdkGenerateRequest request) {
        SdkGeneration generation = generatorService.generate(
                request.getName(),
                request.getLanguage(),
                request.getVersion(),
                request.getApiIds()
        );
        return toGenResponse(generation);
    }

    @GetMapping("/projects")
    @Operation(summary = "SDK 项目列表")
    public PageResult<SdkProjectResponse> listProjects(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        IPage<SdkProject> result = projectRepository.findPage(page, size);
        return PageResult.of(
                result.getRecords().stream().map(this::toProjectResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/projects/{id}")
    @Operation(summary = "SDK 项目详情")
    public SdkProjectResponse getProject(@PathVariable Long id) {
        SdkProject project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SdkProject", id));
        return toProjectResponse(project);
    }

    @GetMapping("/projects/{id}/generations")
    @Operation(summary = "SDK 生成记录列表")
    public PageResult<SdkGenerationResponse> listGenerations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        IPage<SdkGeneration> result = generationRepository.findByProjectId(id, page, size);
        return PageResult.of(
                result.getRecords().stream().map(this::toGenResponse).toList(),
                page, size, result.getTotal()
        );
    }

    @GetMapping("/generations/{id}")
    @Operation(summary = "SDK 生成记录详情")
    public SdkGenerationResponse getGeneration(@PathVariable Long id) {
        SdkGeneration generation = generationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SdkGeneration", id));
        return toGenResponse(generation);
    }

    @GetMapping("/generations/{id}/download")
    @Operation(summary = "下载 SDK")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        SdkGeneration generation = generationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SdkGeneration", id));

        if (!"READY".equals(generation.getStatus())) {
            throw new IllegalStateException("SDK 尚未生成完毕，当前状态: " + generation.getStatus());
        }

        Path zipPath = Path.of("data/sdk", "gen-" + id + ".zip");
        if (!zipPath.toFile().exists()) {
            throw new ResourceNotFoundException("SDK file", id);
        }

        Resource resource = new FileSystemResource(zipPath);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"sdk-gen-" + id + ".zip\"")
                .body(resource);
    }

    @DeleteMapping("/projects/{id}")
    @Operation(summary = "删除 SDK 项目")
    public void deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }

    private SdkProjectResponse toProjectResponse(SdkProject project) {
        return SdkProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .language(project.getLanguage())
                .version(project.getVersion())
                .status(project.getStatus())
                .errorMessage(project.getErrorMessage())
                .createTime(project.getCreateTime())
                .updateTime(project.getUpdateTime())
                .build();
    }

    private SdkGenerationResponse toGenResponse(SdkGeneration gen) {
        return SdkGenerationResponse.builder()
                .id(gen.getId())
                .sdkProjectId(gen.getSdkProjectId())
                .apiIds(gen.getApiIds())
                .apiVersion(gen.getApiVersion())
                .generatorVersion(gen.getGeneratorVersion())
                .status(gen.getStatus())
                .downloadUrl(gen.getDownloadUrl())
                .fileSize(gen.getFileSize())
                .errorMessage(gen.getErrorMessage())
                .createTime(gen.getCreateTime())
                .updateTime(gen.getUpdateTime())
                .build();
    }
}
