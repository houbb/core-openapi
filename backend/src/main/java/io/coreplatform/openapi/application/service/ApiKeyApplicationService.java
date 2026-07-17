package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateApiKeyCommand;
import io.coreplatform.openapi.application.domain.ApiKey;
import io.coreplatform.openapi.application.port.ApiKeyRepository;
import io.coreplatform.openapi.application.port.ApplicationRepository;
import io.coreplatform.openapi.infrastructure.crypto.KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyApplicationService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApplicationRepository applicationRepository;
    private final KeyGenerator keyGenerator;

    /**
     * Generate a new API key. The raw key is returned in the ApiKey object's rawKey field.
     * This is the ONLY time the raw key is available.
     */
    @Transactional
    public ApiKey createApiKey(CreateApiKeyCommand command) {
        // Verify application exists
        applicationRepository.findById(command.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("应用不存在: " + command.getApplicationId()));

        // Generate key
        String env = command.getEnvironment() != null ? command.getEnvironment() : "LIVE";
        KeyGenerator.KeyPair keyPair = keyGenerator.generate(env);

        // Parse expiration if provided
        LocalDateTime expireTime = null;
        if (command.getExpireTime() != null && !command.getExpireTime().isBlank()) {
            expireTime = LocalDateTime.parse(command.getExpireTime());
        }

        ApiKey apiKey = ApiKey.builder()
                .applicationId(command.getApplicationId())
                .keyPrefix(keyPair.keyPrefix())
                .keyHash(keyPair.keyHash())
                .name(command.getName() != null ? command.getName() : "")
                .environment(env)
                .status("ACTIVE")
                .expireTime(expireTime)
                .createdTime(LocalDateTime.now())
                .rawKey(keyPair.rawKey()) // Only set here, never stored in DB
                .build();

        ApiKey saved = apiKeyRepository.save(apiKey);
        // Attach raw key to the saved entity for the response
        saved.setRawKey(keyPair.rawKey());
        return saved;
    }

    public Optional<ApiKey> findById(Long id) {
        return apiKeyRepository.findById(id);
    }

    public List<ApiKey> findByApplicationId(Long applicationId) {
        return apiKeyRepository.findByApplicationId(applicationId);
    }

    @Transactional
    public ApiKey disableKey(Long id) {
        ApiKey existing = apiKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API Key不存在: " + id));
        existing.setStatus("DISABLED");
        return apiKeyRepository.save(existing);
    }

    @Transactional
    public ApiKey enableKey(Long id) {
        ApiKey existing = apiKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API Key不存在: " + id));
        existing.setStatus("ACTIVE");
        return apiKeyRepository.save(existing);
    }

    @Transactional
    public void deleteKey(Long id) {
        apiKeyRepository.deleteById(id);
    }

    public Long countActive() {
        return apiKeyRepository.countByStatus("ACTIVE");
    }
}