package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.application.command.CreateApiKeyCommand;
import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.domain.Application;
import io.coreplatform.openapi.application.port.ApiKeyRepository;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.application.service.ApiKeyApplicationService;
import io.coreplatform.openapi.portal.api.request.CreateCredentialRequest;
import io.coreplatform.openapi.portal.api.response.CredentialResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final ApiKeyApplicationService apiKeyService;
    private final ApiKeyRepository apiKeyRepository;
    private final ApplicationRepository applicationRepository;

    public List<CredentialResponse> listCredentials(Long userId, Long appId) {
        validateAppOwnership(userId, appId);
        return apiKeyRepository.findByApplicationId(appId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CredentialResponse createCredential(Long userId, Long appId, CreateCredentialRequest request) {
        validateAppOwnership(userId, appId);

        CreateApiKeyCommand command = new CreateApiKeyCommand();
        command.setApplicationId(appId);
        command.setName(request.getName());
        command.setEnvironment(request.getEnvironment() != null ? request.getEnvironment() : "PRODUCTION");

        ApiKey key = apiKeyService.createApiKey(command);
        CredentialResponse resp = toResponse(key);
        resp.setRawKey(key.getRawKey());
        return resp;
    }

    public CredentialResponse getCredentialDetail(Long userId, Long keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("凭证不存在: " + keyId));
        validateAppOwnership(userId, key.getApplicationId());
        return toResponse(key);
    }

    @Transactional
    public void revokeCredential(Long userId, Long keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new IllegalArgumentException("凭证不存在: " + keyId));
        validateAppOwnership(userId, key.getApplicationId());
        apiKeyRepository.deleteById(keyId);
    }

    private void validateAppOwnership(Long userId, Long appId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + appId));
        if (!userId.equals(app.getOwnerId())) {
            throw new IllegalArgumentException("无权操作此应用");
        }
    }

    private CredentialResponse toResponse(ApiKey key) {
        return CredentialResponse.builder()
                .id(key.getId())
                .name(key.getName())
                .keyPrefix(key.getKeyPrefix())
                .rawKey(null)
                .environment(key.getEnvironment())
                .status(key.getStatus())
                .expireTime(key.getExpireTime())
                .lastUsedTime(key.getLastUsedTime())
                .createdTime(key.getCreatedTime())
                .build();
    }
}