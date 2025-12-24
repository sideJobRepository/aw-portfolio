package io.awportfoiioapi.role.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.role.entity.QRole;
import io.awportfoiioapi.role.entity.Role;
import io.awportfoiioapi.role.repository.query.RoleQueryRepository;
import lombok.RequiredArgsConstructor;

import static io.awportfoiioapi.role.entity.QRole.*;

@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Role findByRoleName(String roleName) {
        return queryFactory
                .select(role)
                .from(role)
                .where(role.roleName.eq(roleName))
                .fetchFirst();
    }
}
