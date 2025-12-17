package io.awportfoiioapi.memberrole.repository.query;

import io.awportfoiioapi.memberrole.entity.MemberRole;

import java.util.Optional;

public interface MemberRoleQueryRepository {
    
    Optional<MemberRole> findByPortfolioMemberId(Long memberId);
}
