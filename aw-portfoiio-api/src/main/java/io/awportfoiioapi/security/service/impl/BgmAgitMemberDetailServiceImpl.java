package io.awportfoiioapi.security.service.impl;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class BgmAgitMemberDetailServiceImpl implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    private final MemberRoleRepository memberRoleRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
    
    public UserDetails loadUserByUsername(Member socialProfile) {
        
//        Member findBgmAgitMember = memberRepository.findByBgmAgitMemberSocialId(String.valueOf(socialProfile.sub()))
//                .orElseGet(() -> {
//                    BgmAgitMember agitMember = new BgmAgitMember(socialProfile);
//                    BgmAgitMember saveMember = bgmAgitMemberRepository.save(agitMember);
//
//                    BgmAgitRole findbyBgmAgitRole = bgmAgitMemberDetailRepository.findByBgmAgitRoleName("USER");
//
//                    BgmAgitMemberRole bgmAgitMemberRole = new BgmAgitMemberRole(saveMember, findbyBgmAgitRole);
//
//                    bgmAgitMemberRoleRepository.save(bgmAgitMemberRole);
//                    eventPublisher.publishEvent(new MemberJoinedEvent(saveMember.getBgmAgitMemberId()));
//                    return saveMember;
//                });
//
//        Long id = findBgmAgitMember.getBgmAgitMemberId();
//        List<String> roleName = bgmAgitMemberDetailRepository.getRoleName(id);
//        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleName);
//
//        return new BgmAgitMemberContext(findBgmAgitMember, authorityList);
        return null;
    }
    
    
}
