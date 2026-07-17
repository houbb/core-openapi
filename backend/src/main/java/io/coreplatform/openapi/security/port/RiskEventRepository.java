package io.coreplatform.openapi.security.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.security.domain.RiskEvent;

import java.util.List;

public interface RiskEventRepository {

    RiskEvent save(RiskEvent event);

    IPage<RiskEvent> findPage(long page, long size, String riskType, String identityId);

    List<RiskEvent> findByIdentityId(String identityId, int limit);
}