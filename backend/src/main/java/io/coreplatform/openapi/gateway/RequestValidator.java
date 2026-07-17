package io.coreplatform.openapi.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.coreplatform.openapi.application.domain.Parameter;
import io.coreplatform.openapi.application.domain.RequestSchema;
import io.coreplatform.openapi.application.port.ParameterRepository;
import io.coreplatform.openapi.application.port.RequestSchemaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final ParameterRepository parameterRepository;
    private final RequestSchemaRepository requestSchemaRepository;
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    /**
     * Validate an incoming gateway request against stored API definitions.
     *
     * @param apiId   the API definition ID
     * @param request the HTTP servlet request
     * @return list of validation error messages (empty if valid)
     */
    public List<String> validate(Long apiId, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        // 1. Validate path/query/header parameters
        List<Parameter> parameters = parameterRepository.findByApiId(apiId);
        for (Parameter param : parameters) {
            String value = resolveParamValue(param, request);
            if (Boolean.TRUE.equals(param.getRequired()) && (value == null || value.isBlank())) {
                errors.add("缺少必填参数: " + param.getName() + " (" + param.getLocation() + ")");
            }
        }

        // 2. Validate request body against JSON Schema (for POST/PUT/PATCH)
        String method = request.getMethod().toUpperCase();
        if (("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method))
                && request.getContentType() != null
                && request.getContentType().contains("application/json")) {

            Optional<RequestSchema> schemaOpt = requestSchemaRepository.findByApiId(apiId);
            if (schemaOpt.isPresent() && schemaOpt.get().getSchemaJson() != null
                    && !schemaOpt.get().getSchemaJson().isBlank()) {
                try {
                    String body = readBody(request);
                    if (body != null && !body.isBlank()) {
                        JsonNode bodyNode = objectMapper.readTree(body);
                        JsonSchema schema = schemaFactory.getSchema(schemaOpt.get().getSchemaJson());
                        Set<ValidationMessage> schemaErrors = schema.validate(bodyNode);
                        for (ValidationMessage msg : schemaErrors) {
                            errors.add("Body校验失败: " + msg.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Schema validation error for apiId={}: {}", apiId, e.getMessage());
                    // Don't fail the request if schema validation itself errors — just log
                }
            }
        }

        return errors;
    }

    private String resolveParamValue(Parameter param, HttpServletRequest request) {
        return switch (param.getLocation().toUpperCase()) {
            case "PATH" -> {
                // Extract from request URI — best effort, the router already matched
                yield null; // PATH params are matched by router, skip validation here
            }
            case "QUERY" -> request.getParameter(param.getName());
            case "HEADER" -> request.getHeader(param.getName());
            default -> null;
        };
    }

    private String readBody(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            log.debug("Failed to read request body: {}", e.getMessage());
            return null;
        }
    }
}