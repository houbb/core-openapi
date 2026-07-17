package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.request.PlaygroundRequest;
import io.coreplatform.openapi.portal.api.response.PlaygroundResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiPlaygroundService {

    private final RestTemplate restTemplate = new RestTemplate();

    public PlaygroundResponse execute(Long userId, PlaygroundRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            HttpHeaders headers = new HttpHeaders();
            if (request.getHeaders() != null) {
                request.getHeaders().forEach(headers::add);
            }
            headers.set("Authorization", "Bearer " + request.getApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    request.getUrl(),
                    HttpMethod.valueOf(request.getMethod().toUpperCase()),
                    entity,
                    String.class
            );

            long latency = System.currentTimeMillis() - startTime;

            Map<String, String> responseHeaders = new HashMap<>();
            response.getHeaders().forEach((key, values) ->
                    responseHeaders.put(key, String.join(", ", values)));

            return PlaygroundResponse.builder()
                    .statusCode(response.getStatusCode().value())
                    .responseHeaders(responseHeaders)
                    .body(response.getBody())
                    .latencyMs(latency)
                    .build();
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            return PlaygroundResponse.builder()
                    .statusCode(500)
                    .responseHeaders(Collections.emptyMap())
                    .body("{\"error\": \"" + e.getMessage() + "\"}")
                    .latencyMs(latency)
                    .build();
        }
    }
}