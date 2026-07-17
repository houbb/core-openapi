package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateDefinitionCommand;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefinitionApplicationService {

    private final DefinitionRepository definitionRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public Definition createDefinition(CreateDefinitionCommand command) {
        // Verify service exists
        serviceRepository.findById(command.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("服务不存在: " + command.getServiceId()));

        Definition definition = Definition.builder()
                .serviceId(command.getServiceId())
                .name(command.getName())
                .path(command.getPath())
                .httpMethod(command.getHttpMethod().toUpperCase())
                .description(command.getDescription())
                .category(command.getCategory())
                .status("DRAFT")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return definitionRepository.save(definition);
    }

    @Transactional
    public Definition updateDefinition(Long id, CreateDefinitionCommand command) {
        Definition existing = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + id));

        existing.setServiceId(command.getServiceId());
        existing.setName(command.getName());
        existing.setPath(command.getPath());
        existing.setHttpMethod(command.getHttpMethod().toUpperCase());
        existing.setDescription(command.getDescription());
        existing.setCategory(command.getCategory());
        existing.setUpdateTime(LocalDateTime.now());
        existing.setUpdateUser("system");
        return definitionRepository.save(existing);
    }

    public Optional<Definition> findById(Long id) {
        return definitionRepository.findById(id);
    }

    public IPage<Definition> findPage(long page, long size, Long serviceId, String keyword, String status) {
        return definitionRepository.findPage(page, size, serviceId, keyword, status);
    }

    @Transactional
    public void deleteDefinition(Long id) {
        definitionRepository.deleteById(id);
    }

    @Transactional
    public Definition publish(Long id) {
        Definition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + id));

        if (!"DRAFT".equals(definition.getStatus()) && !"REVIEW".equals(definition.getStatus())) {
            throw new IllegalStateException("只有 DRAFT 或 REVIEW 状态才能发布，当前状态: " + definition.getStatus());
        }

        definition.setStatus("PUBLISHED");
        definition.setUpdateTime(LocalDateTime.now());
        definition.setUpdateUser("system");
        return definitionRepository.save(definition);
    }

    @Transactional
    public Definition deprecate(Long id) {
        Definition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + id));

        if (!"PUBLISHED".equals(definition.getStatus())) {
            throw new IllegalStateException("只有 PUBLISHED 状态才能废弃，当前状态: " + definition.getStatus());
        }

        definition.setStatus("DEPRECATED");
        definition.setUpdateTime(LocalDateTime.now());
        definition.setUpdateUser("system");
        return definitionRepository.save(definition);
    }

    @Transactional
    public Definition offline(Long id) {
        Definition definition = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + id));

        if (!"DEPRECATED".equals(definition.getStatus())) {
            throw new IllegalStateException("只有 DEPRECATED 状态才能下线，当前状态: " + definition.getStatus());
        }

        definition.setStatus("OFFLINE");
        definition.setUpdateTime(LocalDateTime.now());
        definition.setUpdateUser("system");
        return definitionRepository.save(definition);
    }

    public IPage<Definition> findByServiceId(Long serviceId, long page, long size) {
        return definitionRepository.findByServiceId(serviceId, page, size);
    }
}
