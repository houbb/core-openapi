package io.coreplatform.openapi.portal.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.portal.application.domain.ApiSubscription;

import java.util.List;
import java.util.Optional;

public interface ApiSubscriptionRepository {
    ApiSubscription save(ApiSubscription subscription);
    Optional<ApiSubscription> findById(Long id);
    Optional<ApiSubscription> findByUserAndApi(Long userId, Long apiId);
    List<ApiSubscription> findByUserId(Long userId);
    IPage<ApiSubscription> findPage(long page, long size, Long userId);
    void deleteById(Long id);
    Long countByUserId(Long userId);
}
