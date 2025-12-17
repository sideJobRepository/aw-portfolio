package io.awportfoiioapi.member.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.awportfoiioapi.member.entrity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<Member>  findByPortfolioMemberId(String name) {
        Member result = queryFactory
                .selectFrom(member)
                .where(member.loginId.eq(name))
                .fetchFirst();
        return Optional.of(result);
    }
}
