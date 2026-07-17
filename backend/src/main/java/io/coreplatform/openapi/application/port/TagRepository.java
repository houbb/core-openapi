package io.coreplatform.openapi.application.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.domain.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Tag save(Tag tag);

    Optional<Tag> findById(Long id);

    IPage<Tag> findPage(long page, long size);

    List<Tag> findAll();

    void deleteById(Long id);

    void saveMapping(Long apiId, List<Long> tagIds);

    void deleteMappingsByApiId(Long apiId);

    List<Tag> findTagsByApiId(Long apiId);
}
