package io.coreplatform.openapi.enterprise.port;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.coreplatform.openapi.enterprise.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByOrgAndUser(Long organizationId, Long userId);
    IPage<Member> findPage(long page, long size, Long organizationId, Long teamId, String role);
    List<Member> findByOrganizationId(Long organizationId);
    List<Member> findByTeamId(Long teamId);
    void deleteById(Long id);
    Long countByOrgId(Long organizationId);
}
