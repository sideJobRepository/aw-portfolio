package io.awportfoiioapi.users.service.impl;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.repository.CommonFileRepository;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.memberrole.repository.MemberRoleRepository;
import io.awportfoiioapi.refresh.repository.RefreshTokenRepository;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import io.awportfoiioapi.users.dto.request.UsersPasswordPostRequest;
import io.awportfoiioapi.users.dto.response.UsersGetResponse;
import io.awportfoiioapi.users.service.UsersService;
import io.awportfoiioapi.utils.S3FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    
    
    private final MemberRepository memberRepository;
    
    private final SubmissionRepository submissionRepository;
    
    private final MemberRoleRepository memberRoleRepository;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    private final CommonFileRepository commonFileRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final S3FileUtils s3FileUtils;
    
    @Override
    public Page<UsersGetResponse> getUsers(Pageable pageable) {
        return memberRepository.findUsers(pageable);
    }
    
    @Override
    public ApiResponse modifyPassword(UsersPasswordPostRequest request) {
        Long memberId = request.getMemberId();
        String password = request.getPassword();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        String encode = passwordEncoder.encode(password);
        member.modifyPassword(encode);
        return new ApiResponse(200, true, "비밀번호가 변경되었습니다.");
    }
    
    @Override
    public ApiResponse deleteUser(Long id) {
        memberRoleRepository.deleteByMember(id);
        refreshTokenRepository.deleteByMember(id);
        
        List<Long> submissionIds = submissionRepository.findBySubmissionIds(id);
        
        /* ---------- 제출이 존재하면 ---------- */
        if (!submissionIds.isEmpty()) {
            
            /* ---------- 파일만 조건부 ---------- */
            List<CommonFile> submissionFiles = commonFileRepository.findBySubmissions(submissionIds);
            
            if (!submissionFiles.isEmpty()) {
                for (CommonFile file : submissionFiles) {
                    s3FileUtils.deleteFile(file.getFileUrl());
                }
                commonFileRepository.deleteBySubmissionsFile(submissionIds);
            }
            submissionRepository.deleteByMemberSubmissions(id);
        }
        
        /* ---------- 회원 삭제 ---------- */
        memberRepository.deleteById(id);
        
        return new ApiResponse(200, true, "회원이 삭제되었습니다.");
    }
}
