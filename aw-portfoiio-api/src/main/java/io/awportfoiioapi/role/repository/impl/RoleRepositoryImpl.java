package io.awportfoiioapi.role.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.role.repository.query.RoleQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleQueryRepository {
    
    private final JPAQueryFactory queryFactory;
}
