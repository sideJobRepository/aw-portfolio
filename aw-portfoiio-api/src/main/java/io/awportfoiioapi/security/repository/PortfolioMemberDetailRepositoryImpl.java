package io.awportfoiioapi.security.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.role.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.awportfoiioapi.member.entrity.QMember.member;
import static io.awportfoiioapi.memberrole.entity.QMemberRole.memberRole;
import static io.awportfoiioapi.role.entity.QRole.role;

@Repository
@RequiredArgsConstructor
public class PortfolioMemberDetailRepositoryImpl {
    
    private final JPAQueryFactory queryFactory;
    
    public Role findByRoleName(String roleName) {
        return queryFactory
                .selectFrom(role)
                .where(role.roleName.eq(roleName))
                .fetchOne();
    }
    
    public List<String> getRoleName(Long id){
        return queryFactory
                .select(role.roleName)
                .from(memberRole)
                .join(memberRole.member,member)
                .join(memberRole.role , role)
                .where(memberRole.member.id.eq(id))
                .fetch();
    }
    
}
