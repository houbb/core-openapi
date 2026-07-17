package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.portal.api.request.PlaygroundRequest;
import io.coreplatform.openapi.portal.api.response.PlaygroundResponse;
import io.coreplatform.openapi.portal.application.service.ApiPlaygroundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portal/playground")
@RequiredArgsConstructor
@Tag(name = "API Playground", description = "API在线测试")
public class ApiPlaygroundController {

    private final ApiPlaygroundService playgroundService;

    @PostMapping("/execute")
    @Operation(summary = "执行API测试")
    public PlaygroundResponse execute(HttpServletRequest request,
                                       @Valid @RequestBody PlaygroundRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return playgroundService.execute(userId, req);
    }
}