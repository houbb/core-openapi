package io.coreplatform.openapi.enterprise.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Member;
import io.coreplatform.openapi.enterprise.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public IPage<Member> findPage(long page, long size, Long organizationId, Long teamId, String role) {
        return memberRepository.findPage(page, size, organizationId, teamId, role);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("成员不存在: " + id));
    }

    public List<Member> findByOrganizationId(Long organizationId) {
        return memberRepository.findByOrganizationId(organizationId);
    }

    public List<Member> findByTeamId(Long teamId) {
        return memberRepository.findByTeamId(teamId);
    }

    @Transactional
    public Member addMember(Long organizationId, Long teamId, Long userId, String role) {
        // Check if already a member
        memberRepository.findByOrgAndUser(organizationId, userId).ifPresent(m -> {
            throw new IllegalArgumentException("用户已是该组织成员");
        });

        Member member = Member.builder()
                .organizationId(organizationId)
                .teamId(teamId)
                .userId(userId)
                .role(role != null ? role : "MEMBER")
                .status("ACTIVE")
                .joinedAt(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser("system")
                .updateUser("system")
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMemberRole(Long id, String role) {
        Member member = findById(id);
        member.setRole(role);
        member.setUpdateTime(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public Member changeTeam(Long id, Long teamId) {
        Member member = findById(id);
        member.setTeamId(teamId);
        member.setUpdateTime(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateStatus(Long id, String status) {
        Member member = findById(id);
        member.setStatus(status);
        member.setUpdateTime(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Transactional
    public void removeMember(Long id) {
        memberRepository.deleteById(id);
    }

    public Long countByOrgId(Long organizationId) {
        return memberRepository.countByOrgId(organizationId);
    }
}