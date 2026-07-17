package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateExampleCommand;
import io.coreplatform.openapi.application.domain.ApiExample;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExampleApplicationService {

    private final ExampleRepository exampleRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public ApiExample createExample(CreateExampleCommand command) {
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + command.getApiId()));

        ApiExample example = ApiExample.builder()
                .apiId(command.getApiId())
                .type(command.getType().toUpperCase())
                .name(command.getName())
                .content(command.getContent())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return exampleRepository.save(example);
    }

    @Transactional
    public ApiExample updateExample(Long id, CreateExampleCommand command) {
        ApiExample existing = exampleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("示例不存在: " + id));

        existing.setType(command.getType().toUpperCase());
        existing.setName(command.getName());
        existing.setContent(command.getContent());
        existing.setUpdateTime(LocalDateTime.now());
        return exampleRepository.save(existing);
    }

    public Optional<ApiExample> findById(Long id) {
        return exampleRepository.findById(id);
    }

    public List<ApiExample> findByApiId(Long apiId) {
        return exampleRepository.findByApiId(apiId);
    }

    @Transactional
    public void deleteExample(Long id) {
        exampleRepository.deleteById(id);
    }
}
