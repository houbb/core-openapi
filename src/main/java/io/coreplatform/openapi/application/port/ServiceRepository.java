package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.ApiService;

import java.util.Optional;

public interface ServiceRepository {

    ApiService save(ApiService service);

    Optional<ApiService> findById(Long id);

    Optional<ApiService> findByServiceCode(String serviceCode);

    IPage<ApiService> findPage(long page, long size, String keyword);

    void deleteById(Long id);

    boolean existsByServiceCode(String serviceCode);
}
