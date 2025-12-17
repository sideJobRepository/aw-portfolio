package io.awportfoiioapi.memberrole.service.impl;

import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import io.awportfoiioapi.memberrole.service.MemberRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberRoleServiceImpl implements MemberRoleService {
    
    private final MemberRoleRepository memberRoleRepository;
    @Override
    public MemberRole getMemberRole(Long memberId) {
        return memberRoleRepository.findByPortfolioMemberId(memberId).orElseThrow(() -> new RuntimeException("MemberRole not found"));
    }
}
