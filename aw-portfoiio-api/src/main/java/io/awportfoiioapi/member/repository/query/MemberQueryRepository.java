package io.awportfoiioapi.member.repository.query;

import io.awportfoiioapi.member.entrity.Member;

import java.util.Optional;

public interface MemberQueryRepository {
    
    Member findByPortfolioAdminId(String name);
    
    Optional<Member> findByPortfolioMemberId(String name);
}
