package io.awportfoiioapi.memberrole.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.query.MemberRoleQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.awportfoiioapi.member.entrity.QMember.member;
import static io.awportfoiioapi.memberrole.entity.QMemberRole.memberRole;

@RequiredArgsConstructor
public class MemberRoleRepositoryImpl implements MemberRoleQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<MemberRole> findByPortfolioMemberId(Long memberId) {
        MemberRole result = queryFactory
                .selectFrom(memberRole)
                .join(memberRole.member, member)
                .where(member.id.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
