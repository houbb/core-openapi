package io.coreplatform.openapi.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.*;

@Slf4j
@Component
public class RequestProxy {

    private final RestClient restClient;

    private static final Set<String> SKIP_HEADERS = Set.of(
            "host", "connection", "transfer-encoding"
    );

    public RequestProxy(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Forward a request to the backend service.
     *
     * @param targetUrl the full backend URL
     * @param method    HTTP method
     * @param request   original HttpServletRequest
     * @param timeout   timeout in milliseconds
     * @return ProxyResult with status code, headers, and body
     */
    public ProxyResult forward(String targetUrl, String method, HttpServletRequest request, int timeout) {
        long startTime = System.currentTimeMillis();

        try {
            // Build request body bytes (for POST/PUT/PATCH)
            byte[] requestBody = supportsBody(method) ? readBodyBytes(request) : null;

            // Build the rest client call
            RestClient.RequestBodySpec requestSpec = restClient
                    .method(buildHttpMethod(method))
                    .uri(URI.create(targetUrl));

            // Copy headers
            copyHeaders(request, requestSpec);

            // Set body if present
            RestClient.RequestHeadersSpec<?> headersSpec;
            if (requestBody != null && requestBody.length > 0) {
                headersSpec = requestSpec.body(requestBody);
            } else {
                headersSpec = requestSpec;
            }

            // Execute
            ResponseEntity<byte[]> response = headersSpec.retrieve().toEntity(byte[].class);

            long costTime = System.currentTimeMillis() - startTime;

            // Extract response headers
            Map<String, String> responseHeaders = new HashMap<>();
            if (response.getHeaders() != null) {
                response.getHeaders().forEach((key, values) ->
                        responseHeaders.put(key, String.join(",", values)));
            }

            return new ProxyResult(
                    response.getStatusCode().value(),
                    responseHeaders,
                    response.getBody(),
                    costTime
            );

        } catch (ResourceAccessException e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("Backend connection error: targetUrl={}, error={}", targetUrl, e.getMessage());

            if (e.getCause() instanceof SocketTimeoutException) {
                throw new GatewayException(GatewayErrorCode.SERVICE_TIMEOUT);
            }
            throw new GatewayException(GatewayErrorCode.SERVICE_UNAVAILABLE);

        } catch (GatewayException e) {
            throw e;

        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("Backend proxy error: targetUrl={}, error={}", targetUrl, e.getMessage(), e);
            throw new GatewayException(GatewayErrorCode.BACKEND_ERROR);
        }
    }

    private HttpMethod buildHttpMethod(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }

    private boolean supportsBody(String method) {
        String m = method.toUpperCase();
        return "POST".equals(m) || "PUT".equals(m) || "PATCH".equals(m);
    }

    private byte[] readBodyBytes(HttpServletRequest request) {
        try {
            return request.getInputStream().readAllBytes();
        } catch (IOException e) {
            log.warn("Failed to read request body: {}", e.getMessage());
            return null;
        }
    }

    private void copyHeaders(HttpServletRequest request, RestClient.RequestBodySpec requestSpec) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (!SKIP_HEADERS.contains(name.toLowerCase())) {
                requestSpec.header(name, request.getHeader(name));
            }
        }
    }

    /**
     * Result of a proxy request.
     */
    public record ProxyResult(int statusCode, Map<String, String> headers, byte[] body, long costTime) {
        public String bodyAsString() {
            if (body == null) return "";
            return new String(body);
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }
}