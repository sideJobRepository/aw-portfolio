package io.awportfoiioapi.memberrole.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.memberrole.repository.query.MemberRoleQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRoleRepositoryImpl implements MemberRoleQueryRepository {
    
    private final JPAQueryFactory queryFactory;
}
