package io.awportfoiioapi.security.service.impl;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import io.awportfoiioapi.role.entity.Role;
import io.awportfoiioapi.security.context.MemberContext;
import io.awportfoiioapi.security.repository.PortfolioMemberDetailRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberDetailServiceImpl implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    private final MemberRoleRepository memberRoleRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final PortfolioMemberDetailRepositoryImpl memberDetailRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    
    public UserDetails loadUserByUsername(String loginId,String password) {
        Member findByMember = memberRepository
                .findByPortfolioMemberId(loginId)
                .orElseGet(() -> {
                    
                    Member agitMember = Member.builder()
                            .loginId(loginId)
                            .password(passwordEncoder.encode(password))
                            .build();
                    
                    Member saveMember = memberRepository.save(agitMember);
                    
                    Role findbyBgmAgitRole =
                            memberDetailRepository.findByBgmAgitRoleName("USER");
                    
                    MemberRole memberRole = MemberRole.builder()
                            .member(saveMember)
                            .role(findbyBgmAgitRole)
                            .build();
                    
                    memberRoleRepository.save(memberRole);
                    
                    return saveMember;
                });
        Long id = findByMember.getId();
        List<String> roleName = memberDetailRepository.getRoleName(id);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleName);
        return new MemberContext(findByMember, authorityList);
    }
    
    
}
