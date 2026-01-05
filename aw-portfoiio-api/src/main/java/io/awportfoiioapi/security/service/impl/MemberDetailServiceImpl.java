package io.awportfoiioapi.security.service.impl;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import io.awportfoiioapi.role.entity.Role;
import io.awportfoiioapi.security.context.MemberContext;
import io.awportfoiioapi.security.repository.PortfolioMemberDetailRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberDetailServiceImpl implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    private final MemberRoleRepository memberRoleRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final PortfolioMemberDetailRepositoryImpl memberDetailRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return null;
    }
    
    public UserDetails loadUserByAdmin(String loginId, String password,String ip) {
        Member findAdmin = memberRepository.findByPortfolioAdminId(loginId);
        if (findAdmin == null) {
            throw new UsernameNotFoundException("존재하지않는 아이디 이거나 비밀번호가 맞지않습니다.");
        }
        if (!passwordEncoder.matches(password, findAdmin.getPassword())) {
            throw new BadCredentialsException("존재하지않는 아이디 이거나 비밀번호가 맞지않습니다.");
        }
        findAdmin.modifyIp(ip);
        Long id = findAdmin.getId();
        List<String> roleName = memberDetailRepository.getRoleName(id);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleName);
        return new MemberContext(findAdmin, authorityList);
    }
    
    public UserDetails loadUserByUsername(String loginId, String password,String ip) {
        // 신규 회원 여부 플래그
         AtomicBoolean isNewMember = new AtomicBoolean(false);
         
         Member member = memberRepository
                 .findByPortfolioMemberId(loginId)
                 .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디이거나 비밀번호가 맞지 않습니다."));
         
         if (!passwordEncoder.matches(password, member.getPassword())) {
             throw new BadCredentialsException(
                     "존재하지 않는 아이디이거나 비밀번호가 맞지 않습니다."
             );
         }
        
        Boolean newIs = member.getNewIs();
        if (!newIs) {
            member.modifyNewIs();
            isNewMember.set(true);
        }
        member.modifyIp(ip);
         Long memberId = member.getId();
         List<String> roleNames = memberDetailRepository.getRoleName(memberId);
     
         List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roleNames.toArray(new String[0]));
     
         
         return new MemberContext(member, authorities, isNewMember.get());
    }
    
    
}
