package io.coreplatform.openapi.enterprise.api.controller;

import io.coreplatform.openapi.application.domain.Definition;
import io.coreplatform.openapi.application.port.DefinitionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/openapi/definitions")
@RequiredArgsConstructor
@Tag(name = "API Governance", description = "API生命周期治理")
public class ApiGovernanceController {

    private final DefinitionRepository definitionRepository;

    @PostMapping("/{id}/lifecycle")
    @Operation(summary = "API生命周期状态转换")
    public GovernanceResult transition(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long reviewerId) {
        Definition def = definitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API定义不存在: " + id));

        String currentLifecycle = def.getLifecycleStatus();
        if (currentLifecycle == null || currentLifecycle.isEmpty()) {
            currentLifecycle = "DRAFT";
        }

        // Validate transition
        boolean valid = isValidTransition(currentLifecycle, status);
        if (!valid) {
            throw new IllegalArgumentException(
                    "不允许从 " + currentLifecycle + " 转换为 " + status);
        }

        def.setLifecycleStatus(status);
        if ("APPROVED".equals(status) || "DEPRECATED".equals(status)) {
            def.setReviewedAt(LocalDateTime.now());
            if (reviewerId != null) {
                def.setReviewerId(reviewerId);
            }
        }
        def.setUpdateTime(LocalDateTime.now());
        definitionRepository.save(def);

        return new GovernanceResult(id, currentLifecycle, status, "SUCCESS");
    }

    private boolean isValidTransition(String from, String to) {
        return switch (from) {
            case "DRAFT" -> "REVIEW".equals(to);
            case "REVIEW" -> "APPROVED".equals(to) || "DRAFT".equals(to);
            case "APPROVED" -> "PUBLISHED".equals(to) || "DRAFT".equals(to);
            case "PUBLISHED" -> "DEPRECATED".equals(to);
            case "DEPRECATED" -> "DRAFT".equals(to);
            default -> false;
        };
    }

    public record GovernanceResult(Long definitionId, String fromStatus, String toStatus, String result) {}
}