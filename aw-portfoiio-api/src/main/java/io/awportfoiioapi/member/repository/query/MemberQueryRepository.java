package io.awportfoiioapi.member.repository.query;

import io.awportfoiioapi.member.entrity.Member;

import java.util.Optional;

public interface MemberQueryRepository {
    
    Optional<Member> findByPortfolioMemberId(String name);
}
