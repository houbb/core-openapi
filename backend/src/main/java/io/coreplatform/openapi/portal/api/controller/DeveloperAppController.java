package io.coreplatform.openapi.portal.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.request.CreateAppRequest;
import io.coreplatform.openapi.portal.api.response.DeveloperAppResponse;
import io.coreplatform.openapi.portal.application.service.DeveloperAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portal/apps")
@RequiredArgsConstructor
@Tag(name = "Application Center", description = "应用管理中心")
public class DeveloperAppController {

    private final DeveloperAppService appService;

    @GetMapping
    @Operation(summary = "获取我的应用列表")
    public PageResult<DeveloperAppResponse> list(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "20") long size) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return appService.listMyApps(userId, page, size);
    }

    @GetMapping("/{appId}")
    @Operation(summary = "获取应用详情")
    public DeveloperAppResponse getDetail(HttpServletRequest request, @PathVariable Long appId) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return appService.getAppDetail(userId, appId);
    }

    @PostMapping
    @Operation(summary = "创建应用")
    public DeveloperAppResponse create(HttpServletRequest request,
                                        @Valid @RequestBody CreateAppRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return appService.createApp(userId, req);
    }

    @PutMapping("/{appId}")
    @Operation(summary = "更新应用")
    public DeveloperAppResponse update(HttpServletRequest request,
                                        @PathVariable Long appId,
                                        @Valid @RequestBody CreateAppRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return appService.updateApp(userId, appId, req);
    }

    @DeleteMapping("/{appId}")
    @Operation(summary = "删除应用")
    public void delete(HttpServletRequest request, @PathVariable Long appId) {
        Long userId = (Long) request.getAttribute("portalUserId");
        appService.deleteApp(userId, appId);
    }
}