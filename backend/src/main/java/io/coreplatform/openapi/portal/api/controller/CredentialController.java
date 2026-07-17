package io.coreplatform.openapi.portal.api.controller;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.request.CreateCredentialRequest;
import io.coreplatform.openapi.portal.api.response.CredentialResponse;
import io.coreplatform.openapi.portal.application.service.CredentialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portal")
@RequiredArgsConstructor
@Tag(name = "Credential Center", description = "凭证管理中心")
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping("/apps/{appId}/credentials")
    @Operation(summary = "获取应用凭证列表")
    public List<CredentialResponse> list(HttpServletRequest request, @PathVariable Long appId) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return credentialService.listCredentials(userId, appId);
    }

    @PostMapping("/apps/{appId}/credentials")
    @Operation(summary = "创建凭证")
    public CredentialResponse create(HttpServletRequest request,
                                      @PathVariable Long appId,
                                      @Valid @RequestBody CreateCredentialRequest req) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return credentialService.createCredential(userId, appId, req);
    }

    @GetMapping("/credentials/{keyId}")
    @Operation(summary = "获取凭证详情")
    public CredentialResponse getDetail(HttpServletRequest request, @PathVariable Long keyId) {
        Long userId = (Long) request.getAttribute("portalUserId");
        return credentialService.getCredentialDetail(userId, keyId);
    }

    @PostMapping("/credentials/{keyId}/revoke")
    @Operation(summary = "吊销凭证")
    public void revoke(HttpServletRequest request, @PathVariable Long keyId) {
        Long userId = (Long) request.getAttribute("portalUserId");
        credentialService.revokeCredential(userId, keyId);
    }
}