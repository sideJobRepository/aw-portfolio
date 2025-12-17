package io.awportfoiioapi.member.service.impl;

import io.awportfoiioapi.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl {

    private final MemberRepository memberRepository;
    
}
