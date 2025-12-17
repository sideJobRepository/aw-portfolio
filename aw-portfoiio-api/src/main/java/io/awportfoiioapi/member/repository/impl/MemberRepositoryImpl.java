package io.awportfoiioapi.member.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;
}
