package io.coreplatform.openapi.enterprise.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.api.dto.PageResult;
import io.coreplatform.openapi.enterprise.api.request.MemberRequest;
import io.coreplatform.openapi.enterprise.api.response.MemberResponse;
import io.coreplatform.openapi.enterprise.application.service.MemberService;
import io.coreplatform.openapi.enterprise.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/openapi/enterprise/organizations/{orgId}/members")
@RequiredArgsConstructor
@Tag(name = "Enterprise Member", description = "企业成员管理")
public class EnterpriseMemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "获取组织成员列表")
    public PageResult<MemberResponse> list(
            @PathVariable Long orgId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String role) {
        IPage<Member> result = memberService.findPage(page, size, orgId, teamId, role);
        return PageResult.of(
                result.getRecords().stream().map(this::toResponse).toList(),
                page, size, result.getTotal());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取成员详情")
    public MemberResponse get(@PathVariable Long orgId, @PathVariable Long id) {
        return toResponse(memberService.findById(id));
    }

    @PostMapping
    @Operation(summary = "添加成员")
    public MemberResponse create(@PathVariable Long orgId, @Valid @RequestBody MemberRequest request) {
        Member member = memberService.addMember(orgId, request.getTeamId(), request.getUserId(), request.getRole());
        return toResponse(member);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "更新成员角色")
    public MemberResponse updateRole(@PathVariable Long orgId, @PathVariable Long id, @RequestParam String role) {
        return toResponse(memberService.updateMemberRole(id, role));
    }

    @PutMapping("/{id}/team")
    @Operation(summary = "变更成员团队")
    public MemberResponse changeTeam(@PathVariable Long orgId, @PathVariable Long id, @RequestParam Long teamId) {
        return toResponse(memberService.changeTeam(id, teamId));
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "更新成员状态")
    public MemberResponse updateStatus(@PathVariable Long orgId, @PathVariable Long id, @RequestParam String status) {
        return toResponse(memberService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "移除成员")
    public void delete(@PathVariable Long orgId, @PathVariable Long id) {
        memberService.removeMember(id);
    }

    private MemberResponse toResponse(Member m) {
        return MemberResponse.builder()
                .id(m.getId()).organizationId(m.getOrganizationId())
                .teamId(m.getTeamId()).userId(m.getUserId())
                .role(m.getRole()).status(m.getStatus())
                .joinedAt(m.getJoinedAt())
                .createTime(m.getCreateTime()).updateTime(m.getUpdateTime())
                .build();
    }
}