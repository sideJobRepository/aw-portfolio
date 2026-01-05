package io.awportfoiioapi.member.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.query.MemberQueryRepository;
import io.awportfoiioapi.userlist.dto.response.QUserListGetResponse;
import io.awportfoiioapi.userlist.dto.response.UserListGetResponse;
import io.awportfoiioapi.users.dto.response.QUsersGetResponse;
import io.awportfoiioapi.users.dto.response.UsersGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    public Optional<Member> findByPortfolioMemberId(String name) {
        Member result = queryFactory
                .selectFrom(member)
                .where(member.loginId.eq(name))
                .fetchFirst();
        return Optional.ofNullable(result);
    }
    
    @Override
    public Page<UserListGetResponse> findByUserList(Pageable pageable) {
        List<UserListGetResponse> result = queryFactory
                .select(
                        new QUserListGetResponse(
                                member.id,
                                member.loginId,
                                member.name,
                                role.roleName,
                                member.registDate
                        )
                )
                .from(memberRole)
                .join(memberRole.member, member)
                .join(memberRole.role, role)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member);
        
         return PageableExecutionUtils.getPage(result,pageable,countQuery::fetchOne);
    }
    
    @Override
    public Page<UsersGetResponse> findUsers(Pageable pageable) {
        List<UsersGetResponse> result = queryFactory
                .select(
                        new QUsersGetResponse(
                                member.id,
                                member.loginId,
                                member.modifyDate,
                                member.registDate,
                                member.modifyDate,
                                member.ip
                        )
                )
                .from(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member);
        return PageableExecutionUtils.getPage(result,pageable,countQuery::fetchOne);
    }
    
    @Override
    public long getTodaySignupCount() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        
        return queryFactory
                .select(member.count())
                .from(member)
                .where(member.registDate.goe(startOfToday))
                .fetchOne();
    }
    
    @Override
    public boolean findByUsername(String name) {
        return queryFactory
                .selectFrom(member)
                .where(member.loginId.eq(name))
                .fetchFirst() != null;
    }
}
