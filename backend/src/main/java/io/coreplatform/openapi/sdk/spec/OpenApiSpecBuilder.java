package io.coreplatform.openapi.sdk.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.domain.Parameter;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ParameterRepository;
import io.coreplatform.openapi.application.port.ResponseRepository;
import io.coreplatform.openapi.application.domain.ApiResponse;
import io.coreplatform.openapi.application.domain.RequestSchema;
import io.coreplatform.openapi.application.port.RequestSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Dynamically build an OpenAPI 3.0 spec from API Definitions stored in the database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiSpecBuilder {

    private final DefinitionRepository definitionRepository;
    private final ParameterRepository parameterRepository;
    private final ResponseRepository responseRepository;
    private final RequestSchemaRepository requestSchemaRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public String buildSpec(List<Long> apiIds, String sdkName) {
        ObjectNode root = mapper.createObjectNode();
        root.put("openapi", "3.0.3");

        // Info
        ObjectNode info = root.putObject("info");
        info.put("title", sdkName);
        info.put("version", "1.0.0");
        info.put("description", "Auto-generated SDK from core-openapi platform");

        // Servers
        ArrayNode servers = root.putArray("servers");
        ObjectNode server = servers.addObject();
        server.put("url", "http://localhost:8107");
        server.put("description", "local development");

        // Paths
        ObjectNode paths = root.putObject("paths");

        for (Long apiId : apiIds) {
            definitionRepository.findById(apiId).ifPresent(def -> {
                String pathKey = "/gateway" + def.getPath();
                String method = def.getHttpMethod().toLowerCase();

                ObjectNode pathObj;
                if (paths.has(pathKey)) {
                    pathObj = (ObjectNode) paths.get(pathKey);
                } else {
                    pathObj = paths.putObject(pathKey);
                }

                ObjectNode operation = pathObj.putObject(method);
                operation.put("summary", def.getName());
                operation.put("description", def.getDescription() != null ? def.getDescription() : "");
                operation.put("operationId", toOperationId(def.getName(), method));
                ArrayNode tags = operation.putArray("tags");
                if (def.getCategory() != null && !def.getCategory().isEmpty()) {
                    tags.add(def.getCategory());
                }

                // Parameters
                List<Parameter> params = parameterRepository.findByApiId(apiId);
                if (!params.isEmpty()) {
                    ArrayNode paramArray = operation.putArray("parameters");
                    for (Parameter p : params) {
                        if (!"BODY".equalsIgnoreCase(p.getLocation())) {
                            ObjectNode paramNode = paramArray.addObject();
                            paramNode.put("name", p.getName());
                            paramNode.put("in", p.getLocation().toLowerCase());
                            paramNode.put("required", Boolean.TRUE.equals(p.getRequired()));
                            paramNode.put("description", p.getDescription() != null ? p.getDescription() : "");
                            ObjectNode schema = paramNode.putObject("schema");
                            schema.put("type", mapType(p.getType()));
                            if (p.getExample() != null && !p.getExample().isEmpty()) {
                                paramNode.put("example", p.getExample());
                            }
                        }
                    }
                }

                // Request Body
                var requestSchemaOpt = requestSchemaRepository.findByApiId(apiId);
                if (requestSchemaOpt.isPresent()) {
                    RequestSchema rs = requestSchemaOpt.get();
                    ObjectNode requestBody = operation.putObject("requestBody");
                    requestBody.put("required", true);
                    ObjectNode content = requestBody.putObject("content");
                    ObjectNode jsonContent = content.putObject(rs.getContentType() != null ? rs.getContentType() : "application/json");
                    // Parse schema_json if present
                    if (rs.getSchemaJson() != null && !rs.getSchemaJson().isEmpty()) {
                        try {
                            jsonContent.set("schema", mapper.readTree(rs.getSchemaJson()));
                        } catch (Exception e) {
                            log.warn("Failed to parse schema_json for apiId={}: {}", apiId, e.getMessage());
                        }
                    }
                    if (rs.getExampleJson() != null && !rs.getExampleJson().isEmpty()) {
                        try {
                            jsonContent.set("example", mapper.readTree(rs.getExampleJson()));
                        } catch (Exception e) {
                            jsonContent.put("example", rs.getExampleJson());
                        }
                    }
                }

                // Responses
                ObjectNode responses = operation.putObject("responses");
                List<ApiResponse> apiResponses = responseRepository.findByApiId(apiId);
                if (apiResponses.isEmpty()) {
                    ObjectNode defaultResp = responses.putObject("200");
                    defaultResp.put("description", "OK");
                } else {
                    for (ApiResponse resp : apiResponses) {
                        ObjectNode respNode = responses.putObject(resp.getStatusCode());
                        respNode.put("description", resp.getDescription() != null ? resp.getDescription() : "");
                        if (resp.getSchema() != null && !resp.getSchema().isEmpty()) {
                            ObjectNode content = respNode.putObject("content");
                            ObjectNode jsonContent = content.putObject(resp.getContentType() != null ? resp.getContentType() : "application/json");
                            try {
                                jsonContent.set("schema", mapper.readTree(resp.getSchema()));
                            } catch (Exception e) {
                                log.warn("Failed to parse response schema for apiId={}: {}", apiId, e.getMessage());
                            }
                        }
                    }
                }
            });
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write OpenAPI spec", e);
        }
    }

    private String toOperationId(String name, String method) {
        return method + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String mapType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "long", "integer", "int" -> "integer";
            case "boolean", "bool" -> "boolean";
            case "double", "float", "bigdecimal" -> "number";
            default -> "string";
        };
    }
}
