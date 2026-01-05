package io.awportfoiioapi.userlist.service.impl;

import io.awportfoiioapi.advice.exception.CategoryAndPortfolioException;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import io.awportfoiioapi.role.entity.Role;
import io.awportfoiioapi.role.repository.RoleRepository;
import io.awportfoiioapi.userlist.dto.request.UserListPostRequest;
import io.awportfoiioapi.userlist.dto.response.UserListGetResponse;
import io.awportfoiioapi.userlist.service.UserListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserListServiceImpl implements UserListService {
    
    private final MemberRepository memberRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final RoleRepository roleRepository;
    
    private final MemberRoleRepository memberRoleRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserListGetResponse> getUserList(Pageable pageable) {
        return memberRepository.findByUserList(pageable);
    }
    
    @Override
    public ApiResponse createUserList(UserListPostRequest request) {
        
        String role = request.getRole();
        
        boolean byUsername = memberRepository.findByUsername(request.getName());
        if(byUsername) {
            throw new CategoryAndPortfolioException("이미 존재하는 회원 이름입니다.",null);
        }
        
        Member newMember = Member
                .builder()
                .loginId(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .newIs(false)
                .build();
        Member saveMember = memberRepository.save(newMember);
        
        Role userRole = roleRepository.findByRoleName(role);
        MemberRole memberRole = MemberRole.builder()
                .member(saveMember)
                .role(userRole)
                .build();
        memberRoleRepository.save(memberRole);
        return new ApiResponse(200,true,"회원이 생성되었습니다.");
    }
}
