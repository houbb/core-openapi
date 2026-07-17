package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateVersionCommand;
import io.coreplatform.openapi.application.domain.ApiVersion;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VersionApplicationService {

    private final VersionRepository versionRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public ApiVersion createVersion(CreateVersionCommand command) {
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + command.getApiId()));

        ApiVersion version = ApiVersion.builder()
                .apiId(command.getApiId())
                .version(command.getVersion())
                .status("DRAFT")
                .changelog(command.getChangelog())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return versionRepository.save(version);
    }

    @Transactional
    public ApiVersion updateVersion(Long id, CreateVersionCommand command) {
        ApiVersion existing = versionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + id));

        existing.setVersion(command.getVersion());
        existing.setChangelog(command.getChangelog());
        existing.setUpdateTime(LocalDateTime.now());
        return versionRepository.save(existing);
    }

    public Optional<ApiVersion> findById(Long id) {
        return versionRepository.findById(id);
    }

    public List<ApiVersion> findByApiId(Long apiId) {
        return versionRepository.findByApiId(apiId);
    }

    @Transactional
    public ApiVersion activate(Long id) {
        ApiVersion version = versionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + id));

        if (!"DRAFT".equals(version.getStatus())) {
            throw new IllegalStateException("只有 DRAFT 状态的版本才能激活，当前状态: " + version.getStatus());
        }

        version.setStatus("ACTIVE");
        version.setReleaseTime(LocalDateTime.now());
        version.setUpdateTime(LocalDateTime.now());
        return versionRepository.save(version);
    }

    @Transactional
    public ApiVersion deprecate(Long id) {
        ApiVersion version = versionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("版本不存在: " + id));

        if (!"ACTIVE".equals(version.getStatus())) {
            throw new IllegalStateException("只有 ACTIVE 状态的版本才能废弃，当前状态: " + version.getStatus());
        }

        version.setStatus("DEPRECATED");
        version.setDeprecatedTime(LocalDateTime.now());
        version.setUpdateTime(LocalDateTime.now());
        return versionRepository.save(version);
    }

    @Transactional
    public void deleteVersion(Long id) {
        versionRepository.deleteById(id);
    }
}
