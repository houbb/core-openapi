package io.coreplatform.openapi.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.application.command.CreateTagCommand;
import io.coreplatform.openapi.application.domain.Tag;
import io.coreplatform.openapi.application.port.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagApplicationService {

    private final TagRepository tagRepository;

    @Transactional
    public Tag createTag(CreateTagCommand command) {
        Tag tag = Tag.builder()
                .name(command.getName())
                .color(command.getColor() != null ? command.getColor() : "#666")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(Long id, CreateTagCommand command) {
        Tag existing = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("标签不存在: " + id));

        existing.setName(command.getName());
        existing.setColor(command.getColor());
        existing.setUpdateTime(LocalDateTime.now());
        return tagRepository.save(existing);
    }

    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    public IPage<Tag> findPage(long page, long size) {
        return tagRepository.findPage(page, size);
    }

    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    @Transactional
    public void setDefinitionTags(Long apiId, List<Long> tagIds) {
        tagRepository.deleteMappingsByApiId(apiId);
        if (tagIds != null && !tagIds.isEmpty()) {
            tagRepository.saveMapping(apiId, tagIds);
        }
    }

    public List<Tag> getDefinitionTags(Long apiId) {
        return tagRepository.findTagsByApiId(apiId);
    }
}
