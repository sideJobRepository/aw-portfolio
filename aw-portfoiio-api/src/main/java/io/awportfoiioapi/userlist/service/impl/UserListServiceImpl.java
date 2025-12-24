package io.awportfoiioapi.userlist.service.impl;

import io.awportfoiioapi.advice.exception.ValidationException;
import io.awportfoiioapi.advice.response.ErrorMessageResponse;
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
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;


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
        
        ErrorMessageResponse error = new ErrorMessageResponse("400", null);
        if (!StringUtils.hasText(request.getEmail())) {
            error.addValidation("loginId", "아이디는 필수 입력입니다.");
        }
        
        if (!StringUtils.hasText(request.getPassword())) {
            error.addValidation("password", "비밀번호는 필수 입력입니다.");
        }
        
        if (error.getValidation() != null && !error.getValidation().isEmpty()) {
            throw new AuthenticationServiceException("400", new ValidationException(error));
        }
        Optional<Member> member = memberRepository.findByPortfolioMemberId(request.getEmail());
        if (member.isPresent()) {
            throw new AuthenticationServiceException("400", new ValidationException(new ErrorMessageResponse("400", "이미 존재하는 아이디 입니다.")));
        }
        
        String role = request.getRole();
        
        Member newMember = Member
                .builder()
                .loginId(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
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
