package io.coreplatform.openapi.portal.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.application.domain.ApiService;
import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.coreplatform.openapi.application.port.ServiceRepository;
import io.coreplatform.openapi.portal.api.response.ApiCatalogDetailResponse;
import io.coreplatform.openapi.portal.api.response.ApiCatalogItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiCatalogService {

    private final DefinitionRepository definitionRepository;
    private final ServiceRepository serviceRepository;

    public PageResult<ApiCatalogItemResponse> listCatalog(long page, long size, String keyword, String category) {
        IPage<Definition> defPage = definitionRepository.findPage(page, size, null, keyword, "PUBLISHED");
        List<ApiCatalogItemResponse> items = defPage.getRecords().stream()
                .map(this::toCatalogItem)
                .collect(Collectors.toList());
        return PageResult.of(items, page, size, defPage.getTotal());
    }

    public ApiCatalogDetailResponse getApiDetail(Long apiId) {
        Definition def = definitionRepository.findById(apiId)
                .orElseThrow(() -> new IllegalArgumentException("API不存在: " + apiId));

        String serviceName = def.getServiceId() != null
                ? serviceRepository.findById(def.getServiceId()).map(ApiService::getServiceName).orElse("")
                : "";

        return ApiCatalogDetailResponse.builder()
                .id(def.getId())
                .name(def.getName())
                .description(def.getDescription())
                .method(def.getHttpMethod())
                .path(def.getPath())
                .version("")
                .serviceName(serviceName)
                .category(def.getCategory())
                .status(def.getStatus())
                .parameters(Collections.emptyList())
                .responses(Collections.emptyList())
                .requestSchema(null)
                .examples(Collections.emptyList())
                .subscribed(false)
                .sdkAvailability("Java, Python")
                .build();
    }

    public List<ApiCatalogItemResponse> listByService(Long serviceId) {
        IPage<Definition> defPage = definitionRepository.findByServiceId(serviceId, 1, 200);
        return defPage.getRecords().stream()
                .filter(d -> "PUBLISHED".equals(d.getStatus()))
                .map(this::toCatalogItem)
                .collect(Collectors.toList());
    }

    private ApiCatalogItemResponse toCatalogItem(Definition def) {
        String serviceName = def.getServiceId() != null
                ? serviceRepository.findById(def.getServiceId()).map(ApiService::getServiceName).orElse("")
                : "";

        return ApiCatalogItemResponse.builder()
                .id(def.getId())
                .name(def.getName())
                .description(def.getDescription())
                .method(def.getHttpMethod())
                .path(def.getPath())
                .version("")
                .serviceName(serviceName)
                .category(def.getCategory())
                .status(def.getStatus())
                .build();
    }
}