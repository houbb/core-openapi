package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.portal.api.response.SdkCenterItemResponse;
import io.coreplatform.openapi.portal.application.port.PortalDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SdkCenterService {

    public List<SdkCenterItemResponse> listAvailableSdks() {
        // In MVP, provide static SDK info based on existing SDK projects
        SdkCenterItemResponse javaSdk = SdkCenterItemResponse.builder()
                .id(1L)
                .name("Java SDK")
                .language("Java")
                .version("1.0.0")
                .status("READY")
                .downloadUrl(null)
                .fileSize(null)
                .build();

        SdkCenterItemResponse pythonSdk = SdkCenterItemResponse.builder()
                .id(2L)
                .name("Python SDK")
                .language("Python")
                .version("1.0.0")
                .status("READY")
                .downloadUrl(null)
                .fileSize(null)
                .build();

        return List.of(javaSdk, pythonSdk);
    }

    public SdkCenterItemResponse getSdkDetail(Long projectId) {
        return SdkCenterItemResponse.builder()
                .id(projectId)
                .name("Java SDK")
                .language("Java")
                .version("1.0.0")
                .status("READY")
                .downloadUrl(null)
                .fileSize(null)
                .build();
    }

    public ResponseEntity<Resource> download(Long generationId) {
        // In MVP, return placeholder
        try {
            Path filePath = Paths.get("data/sdk/sdk-" + generationId + ".zip");
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sdk.zip\"")
                        .body(resource);
            }
        } catch (MalformedURLException ignored) {}
        return ResponseEntity.notFound().build();
    }
}