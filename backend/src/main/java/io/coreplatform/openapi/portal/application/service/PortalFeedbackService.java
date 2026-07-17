package io.coreplatform.openapi.portal.application.service;

import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.portal.api.request.FeedbackRequest;
import io.coreplatform.openapi.portal.api.response.FeedbackResponse;
import io.coreplatform.openapi.portal.application.domain.PortalFeedback;
import io.coreplatform.openapi.portal.application.port.PortalFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortalFeedbackService {

    private final PortalFeedbackRepository feedbackRepository;

    @Transactional
    public FeedbackResponse submit(Long userId, FeedbackRequest request) {
        PortalFeedback feedback = PortalFeedback.builder()
                .userId(userId)
                .type(request.getType() != null ? request.getType() : "SUGGESTION")
                .title(request.getTitle())
                .content(request.getContent() != null ? request.getContent() : "")
                .status("OPEN")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        PortalFeedback saved = feedbackRepository.save(feedback);
        return toResponse(saved);
    }

    public PageResult<FeedbackResponse> listByUser(Long userId, long page, long size) {
        var fbPage = feedbackRepository.findByUserId(page, size, userId);
        List<FeedbackResponse> items = fbPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResult.of(items, page, size, fbPage.getTotal());
    }

    private FeedbackResponse toResponse(PortalFeedback fb) {
        return FeedbackResponse.builder()
                .id(fb.getId())
                .type(fb.getType())
                .title(fb.getTitle())
                .content(fb.getContent())
                .status(fb.getStatus())
                .adminReply(fb.getAdminReply())
                .createTime(fb.getCreateTime())
                .updateTime(fb.getUpdateTime())
                .build();
    }
}