package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateRequestSchemaCommand;
import io.coreplatform.openapi.application.domain.RequestSchema;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.RequestSchemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestSchemaApplicationService {

    private final RequestSchemaRepository requestSchemaRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public RequestSchema createOrUpdate(CreateRequestSchemaCommand command) {
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + command.getApiId()));

        // Upsert: find existing by apiId, update if exists, create if not
        Optional<RequestSchema> existing = requestSchemaRepository.findByApiId(command.getApiId());
        RequestSchema schema;
        if (existing.isPresent()) {
            schema = existing.get();
            schema.setContentType(command.getContentType() != null ? command.getContentType() : "application/json");
            schema.setSchemaJson(command.getSchemaJson());
            schema.setExampleJson(command.getExampleJson());
            schema.setDescription(command.getDescription());
            schema.setUpdateTime(LocalDateTime.now());
        } else {
            schema = RequestSchema.builder()
                    .apiId(command.getApiId())
                    .contentType(command.getContentType() != null ? command.getContentType() : "application/json")
                    .schemaJson(command.getSchemaJson())
                    .exampleJson(command.getExampleJson())
                    .description(command.getDescription())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
        }
        return requestSchemaRepository.save(schema);
    }

    public Optional<RequestSchema> findByApiId(Long apiId) {
        return requestSchemaRepository.findByApiId(apiId);
    }

    @Transactional
    public void deleteByApiId(Long apiId) {
        requestSchemaRepository.deleteByApiId(apiId);
    }
}
