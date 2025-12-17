package io.awportfoiioapi.hierarchy.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.hierarchy.repository.query.RoleHierarchyQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleHierarchyRepositoryImpl implements RoleHierarchyQueryRepository {

    private final JPAQueryFactory queryFactory;
}
