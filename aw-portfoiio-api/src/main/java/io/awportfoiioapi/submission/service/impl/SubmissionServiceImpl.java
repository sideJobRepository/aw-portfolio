package io.awportfoiioapi.submission.service.impl;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.file.repository.CommonFileRepository;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.member.repository.MemberRepository;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.portfolio.repository.PortfolioRepository;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPostRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPutRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import io.awportfoiioapi.submission.service.SubmissionService;
import io.awportfoiioapi.utils.S3FileUtils;
import io.awportfoiioapi.utils.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    
    private final SubmissionRepository submissionRepository;
    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final CommonFileRepository commonFileRepository;
    private final S3FileUtils s3FileUtils;
    
    @Override
    public List<SubmissionGetRequest> getSubmissions() {
        return List.of();
    }
    
    @Override
    public ApiResponse createSubmission(SubmissionPostRequest request) {
        Long memberId = request.getMemberId();
        Long submissionId = request.getSubmissionId();
        Long portfolioId = request.getPortfolioId();
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포트폴리오입니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Submission submission = Optional.ofNullable(submissionId)
                .flatMap(submissionRepository::findById)
                .orElse(null);
        
        if (submission == null) {
            Submission newSubmission = Submission.builder()
                    .portfolio(portfolio)
                    .member(member)
                    .submissionJson(request.getResponse())
                    .companyName(member.getLoginId())
                    .password(member.getPassword())
                    .completedDate(LocalDateTime.now())
                    .isDraft(false)
                    .build();
            
            Submission saved = submissionRepository.save(newSubmission);
            
            return new ApiResponse(200, true, "저장 되었습니다.", saved.getId());
        }
        
        submission.modifySubmission(request);
        return new ApiResponse(200, true, "저장 되었습니다.", submission.getId());
    }
    
    @Override
    public ApiResponse temporaryStorage(SubmissionPostDraftRequest request) {
        // 1. 파라미터 추출
        Long memberId = request.getMemberId();
        Long submissionId = request.getSubmissionId();
        Long portfolioId = request.getPortfolioId();
        
        // 2. 필수 엔티티 조회
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포트폴리오입니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        
        // 3. submission 조회
        Submission submission = Optional.ofNullable(submissionId)
                .flatMap(submissionRepository::findById)
                .orElse(null);
        
        // 4. 신규 생성 or 기존 수정
        if (submission == null) {
            submission = submissionRepository.save(
                    Submission.builder()
                            .portfolio(portfolio)
                            .member(member)
                            .submissionJson(request.getResponse())
                            .companyName(member.getLoginId())
                            .password(member.getPassword())
                            .isDraft(true)
                            .build()
            );
        } else {
            submission.modifyJson(request);
        }
        
        // 5.옵션별 파일 처리 (신규/기존 공통)
        for (SubmissionPostDraftRequest.OptionFileRequest optionFile
                : request.getOptionFiles()) {
            
            Long optionsId = optionFile.getOptionsId();
            List<MultipartFile> files = optionFile.getFiles();
            
            if (files.isEmpty()) continue;
            
            // 5-1. 기존 옵션 파일 삭제 (덮어쓰기)
            commonFileRepository.deleteByTargetIdAndType(
                    optionsId,
                    CommonFileType.SUBMISSION_OPTION
            );
            
            // 5-2. 새 파일 업로드 & 저장
            for (MultipartFile file : files) {
                
                UploadResult upload = s3FileUtils.storeFile(file, "submission");
                
                CommonFile commonFile = CommonFile.builder()
                        .fileTargetId(submission.getId())
                        .optionsId(optionsId)
                        .questionStep(optionFile.getQuestionStep())
                        .questionOrder(optionFile.getQuestionOrder())
                        .fileName(file.getOriginalFilename())
                        .fileType(CommonFileType.SUBMISSION_OPTION)
                        .fileUrl(upload.url())
                        .build();
                
                commonFileRepository.save(commonFile);
            }
        }
        
        
        return new ApiResponse(200, true, "임시저장 되었습니다.", submission.getId());
        
    }
    
    @Override
    public ApiResponse modifySubmission(SubmissionPutRequest request) {
        return null;
    }
    
    @Override
    public ApiResponse deleteSubmission(Long id) {
        return null;
    }
}
