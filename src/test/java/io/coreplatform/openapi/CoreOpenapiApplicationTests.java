package io.coreplatform.openapi;

import io.coreplatform.openapi.api.dto.ApiError;
import io.coreplatform.openapi.api.request.ServiceRequest;
import io.coreplatform.openapi.api.response.ServiceResponse;
import io.coreplatform.openapi.application.service.ServiceApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CoreOpenapiApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ServiceApplicationService serviceApplicationService;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/openapi";
    }

    @Test
    void contextLoads() {
        // Basic smoke test — application starts successfully
    }

    @Test
    void shouldCreateAndRetrieveService() {
        // Create
        ServiceRequest req = new ServiceRequest();
        req.setServiceName("Test Service");
        req.setServiceCode("test-svc");
        req.setDescription("A test service");

        ResponseEntity<ServiceResponse> createResp = restTemplate.postForEntity(
                baseUrl() + "/services", req, ServiceResponse.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().getServiceCode()).isEqualTo("test-svc");
        assertThat(createResp.getBody().getStatus()).isEqualTo("ACTIVE");

        // Retrieve
        Long id = createResp.getBody().getId();
        ResponseEntity<ServiceResponse> getResp = restTemplate.getForEntity(
                baseUrl() + "/services/" + id, ServiceResponse.class);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().getServiceName()).isEqualTo("Test Service");
    }

    @Test
    void shouldReturnErrorForDuplicateCode() {
        ServiceRequest req = new ServiceRequest();
        req.setServiceName("S1");
        req.setServiceCode("dup-code");

        restTemplate.postForEntity(baseUrl() + "/services", req, ServiceResponse.class);
        ResponseEntity<ApiError> dupResp = restTemplate.postForEntity(
                baseUrl() + "/services", req, ApiError.class);

        assertThat(dupResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(dupResp.getBody()).isNotNull();
        assertThat(dupResp.getBody().getErrorCode()).isEqualTo("INVALID_ARGUMENT");
    }

    @Test
    void shouldReturn404ForNonExistent() {
        ResponseEntity<ApiError> resp = restTemplate.getForEntity(
                baseUrl() + "/services/99999", ApiError.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnDashboard() {
        ResponseEntity<java.util.Map> resp = restTemplate.getForEntity(
                baseUrl() + "/dashboard", java.util.Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).containsKeys("serviceCount", "definitionCount", "publishedCount");
    }
}