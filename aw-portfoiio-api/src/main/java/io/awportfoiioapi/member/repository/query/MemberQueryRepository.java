package io.awportfoiioapi.member.repository.query;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.userlist.dto.response.UserListGetResponse;
import io.awportfoiioapi.users.dto.response.UsersGetResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberQueryRepository {
    
    Member findByPortfolioAdminId(String name);
    
    Optional<Member> findByPortfolioMemberId(String name);
    
    Page<UserListGetResponse> findByUserList(Pageable pageable);
    
    Page<UsersGetResponse> findUsers(Pageable pageable);
    long getTodaySignupCount();
    
    boolean findByUsername(String name);
    
}
