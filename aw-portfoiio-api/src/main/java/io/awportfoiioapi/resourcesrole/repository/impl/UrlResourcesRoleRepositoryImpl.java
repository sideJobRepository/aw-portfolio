package io.awportfoiioapi.resourcesrole.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.resourcesrole.repository.query.UrlResourcesRoleQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UrlResourcesRoleRepositoryImpl implements UrlResourcesRoleQueryRepository {

    private final JPAQueryFactory queryFactory;
}
