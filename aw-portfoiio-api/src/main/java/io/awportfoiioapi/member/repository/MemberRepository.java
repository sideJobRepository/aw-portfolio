package io.awportfoiioapi.member.repository;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.query.MemberQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> , MemberQueryRepository {
}
