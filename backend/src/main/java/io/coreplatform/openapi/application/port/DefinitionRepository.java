package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.Definition;

import java.util.Optional;

public interface DefinitionRepository {

    Definition save(Definition definition);

    Optional<Definition> findById(Long id);

    IPage<Definition> findPage(long page, long size, Long serviceId, String keyword, String status);

    IPage<Definition> findByServiceId(Long serviceId, long page, long size);

    void deleteById(Long id);

    Long countByStatus(String status);
}
