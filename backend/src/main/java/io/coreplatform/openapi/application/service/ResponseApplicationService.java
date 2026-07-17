package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.command.CreateResponseCommand;
import io.coreplatform.openapi.application.domain.ApiResponse;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResponseApplicationService {

    private final ResponseRepository responseRepository;
    private final DefinitionRepository definitionRepository;

    @Transactional
    public ApiResponse createResponse(CreateResponseCommand command) {
        definitionRepository.findById(command.getApiId())
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + command.getApiId()));

        ApiResponse response = ApiResponse.builder()
                .apiId(command.getApiId())
                .statusCode(command.getStatusCode())
                .contentType(command.getContentType() != null ? command.getContentType() : "application/json")
                .schema(command.getSchema())
                .example(command.getExample())
                .description(command.getDescription())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return responseRepository.save(response);
    }

    @Transactional
    public ApiResponse updateResponse(Long id, CreateResponseCommand command) {
        ApiResponse existing = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("响应定义不存在: " + id));

        existing.setStatusCode(command.getStatusCode());
        existing.setContentType(command.getContentType() != null ? command.getContentType() : "application/json");
        existing.setSchema(command.getSchema());
        existing.setExample(command.getExample());
        existing.setDescription(command.getDescription());
        existing.setUpdateTime(LocalDateTime.now());
        return responseRepository.save(existing);
    }

    public Optional<ApiResponse> findById(Long id) {
        return responseRepository.findById(id);
    }

    public List<ApiResponse> findByApiId(Long apiId) {
        return responseRepository.findByApiId(apiId);
    }

    @Transactional
    public void deleteResponse(Long id) {
        responseRepository.deleteById(id);
    }
}
