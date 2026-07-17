package io.coreplatform.openapi.application.service;

import io.coreplatform.openapi.application.domain.*;
import io.coreplatform.openapi.application.port.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentationApplicationService {

    private final DefinitionRepository definitionRepository;
    private final ServiceRepository serviceRepository;
    private final ParameterRepository parameterRepository;
    private final ResponseRepository responseRepository;
    private final RequestSchemaRepository requestSchemaRepository;
    private final ExampleRepository exampleRepository;
    private final TagRepository tagRepository;

    /**
     * Generate aggregated documentation for a single API definition.
     */
    public Map<String, Object> getApiDocument(Long apiId) {
        Definition definition = definitionRepository.findById(apiId)
                .orElseThrow(() -> new IllegalArgumentException("接口定义不存在: " + apiId));

        ApiService service = serviceRepository.findById(definition.getServiceId()).orElse(null);
        List<Parameter> parameters = parameterRepository.findByApiId(apiId);
        List<ApiResponse> responses = responseRepository.findByApiId(apiId);
        Optional<RequestSchema> requestSchema = requestSchemaRepository.findByApiId(apiId);
        List<ApiExample> examples = exampleRepository.findByApiId(apiId);
        List<Tag> tags = tagRepository.findTagsByApiId(apiId);

        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("id", definition.getId());
        doc.put("name", definition.getName());
        doc.put("path", definition.getPath());
        doc.put("httpMethod", definition.getHttpMethod());
        doc.put("description", definition.getDescription());
        doc.put("category", definition.getCategory());
        doc.put("status", definition.getStatus());
        doc.put("service", service != null ? Map.of(
                "id", service.getId(),
                "name", service.getServiceName(),
                "code", service.getServiceCode()
        ) : null);

        // Parameters grouped by location
        Map<String, List<Map<String, Object>>> groupedParams = new LinkedHashMap<>();
        for (Parameter p : parameters) {
            groupedParams.computeIfAbsent(p.getLocation(), k -> new ArrayList<>())
                    .add(Map.of(
                            "id", p.getId(),
                            "name", p.getName(),
                            "type", p.getType(),
                            "required", p.getRequired(),
                            "description", p.getDescription() != null ? p.getDescription() : "",
                            "example", p.getExample() != null ? p.getExample() : ""
                    ));
        }
        doc.put("parameters", groupedParams);

        // Request Schema
        doc.put("requestSchema", requestSchema.map(rs -> Map.of(
                "contentType", rs.getContentType(),
                "schemaJson", rs.getSchemaJson() != null ? rs.getSchemaJson() : "",
                "exampleJson", rs.getExampleJson() != null ? rs.getExampleJson() : "",
                "description", rs.getDescription() != null ? rs.getDescription() : ""
        )).orElse(null));

        // Responses
        doc.put("responses", responses.stream().map(r -> Map.of(
                "statusCode", r.getStatusCode(),
                "contentType", r.getContentType(),
                "schema", r.getSchema() != null ? r.getSchema() : "",
                "example", r.getExample() != null ? r.getExample() : "",
                "description", r.getDescription() != null ? r.getDescription() : ""
        )).collect(Collectors.toList()));

        // Examples
        doc.put("examples", examples.stream().map(e -> Map.of(
                "id", e.getId(),
                "type", e.getType(),
                "name", e.getName() != null ? e.getName() : "",
                "content", e.getContent() != null ? e.getContent() : ""
        )).collect(Collectors.toList()));

        // Tags
        doc.put("tags", tags.stream().map(t -> Map.of(
                "id", t.getId(),
                "name", t.getName(),
                "color", t.getColor()
        )).collect(Collectors.toList()));

        return doc;
    }

    /**
     * Generate documentation for all published API definitions.
     */
    public List<Map<String, Object>> getAllPublishedDocs() {
        // Use findPage with status=PUBLISHED to get all published
        List<Definition> published = new ArrayList<>();
        // Paginate through all published definitions
        long page = 1;
        long size = 100;
        while (true) {
            var pageResult = definitionRepository.findPage(page, size, null, null, "PUBLISHED");
            published.addAll(pageResult.getRecords());
            if (pageResult.getRecords().size() < size) break;
            page++;
        }

        return published.stream()
                .map(def -> {
                    try {
                        return getApiDocument(def.getId());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Generate documentation for all APIs in a service.
     */
    public List<Map<String, Object>> getServiceDocs(Long serviceId) {
        List<Definition> definitions = new ArrayList<>();
        long page = 1;
        long size = 100;
        while (true) {
            var pageResult = definitionRepository.findByServiceId(serviceId, page, size);
            definitions.addAll(pageResult.getRecords());
            if (pageResult.getRecords().size() < size) break;
            page++;
        }

        return definitions.stream()
                .map(def -> {
                    try {
                        return getApiDocument(def.getId());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}