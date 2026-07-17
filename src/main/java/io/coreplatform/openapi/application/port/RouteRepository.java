package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {

    Route save(Route route);

    Optional<Route> findById(Long id);

    Optional<Route> findByApiId(Long apiId);

    List<Route> findActiveRoutes();

    IPage<Route> findPage(long page, long size, Long apiId, String serviceName, String status);

    void deleteById(Long id);

    Long countByStatus(String status);
}