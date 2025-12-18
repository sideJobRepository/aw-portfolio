package io.awportfoiioapi.member.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.awportfoiioapi.member.entrity.QMember.member;
import static io.awportfoiioapi.memberrole.entity.QMemberRole.memberRole;
import static io.awportfoiioapi.role.entity.QRole.role;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Member findByPortfolioAdminId(String name) {
        return queryFactory
                .select(member)
                .from(memberRole)
                .join(memberRole.member, member)
                .join(memberRole.role, role)
                .where(role.id.in(1L, 2L), member.loginId.eq(name))
                .fetchFirst();
    }
    
    @Override
    public Optional<Member>  findByPortfolioMemberId(String name) {
        Member result = queryFactory
                .selectFrom(member)
                .where(member.loginId.eq(name))
                .fetchFirst();
        return Optional.of(result);
    }
}
