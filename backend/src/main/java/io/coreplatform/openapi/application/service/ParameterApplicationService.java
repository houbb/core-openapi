package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateParameterCommand;
import io.coreplatform.openapi.application.domain.Parameter;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParameterApplicationService {

    private final ParameterRepository parameterRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public Parameter createParameter(CreateParameterCommand command) {
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + command.getApiId()));

        Parameter parameter = Parameter.builder()
                .apiId(command.getApiId())
                .name(command.getName())
                .location(command.getLocation().toUpperCase())
                .type(command.getType())
                .required(command.getRequired() != null ? command.getRequired() : false)
                .description(command.getDescription())
                .example(command.getExample())
                .sortOrder(command.getSortOrder() != null ? command.getSortOrder() : 0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return parameterRepository.save(parameter);
    }

    @Transactional
    public Parameter updateParameter(Long id, CreateParameterCommand command) {
        Parameter existing = parameterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("参数不存在: " + id));

        existing.setName(command.getName());
        existing.setLocation(command.getLocation().toUpperCase());
        existing.setType(command.getType());
        existing.setRequired(command.getRequired() != null ? command.getRequired() : false);
        existing.setDescription(command.getDescription());
        existing.setExample(command.getExample());
        existing.setSortOrder(command.getSortOrder() != null ? command.getSortOrder() : 0);
        existing.setUpdateTime(LocalDateTime.now());
        return parameterRepository.save(existing);
    }

    public Optional<Parameter> findById(Long id) {
        return parameterRepository.findById(id);
    }

    public List<Parameter> findByApiId(Long apiId) {
        return parameterRepository.findByApiId(apiId);
    }

    @Transactional
    public void deleteParameter(Long id) {
        parameterRepository.deleteById(id);
    }
}