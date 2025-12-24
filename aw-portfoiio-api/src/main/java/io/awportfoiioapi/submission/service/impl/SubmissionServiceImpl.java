package io.awportfoiioapi.submission.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.awportfoiioapi.submission.dto.response.SubmissionGetListRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import io.awportfoiioapi.submission.service.SubmissionService;
import io.awportfoiioapi.utils.S3FileUtils;
import io.awportfoiioapi.utils.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {
    
    private final SubmissionRepository submissionRepository;
    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final CommonFileRepository commonFileRepository;
    private final S3FileUtils s3FileUtils;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    
    
    @Override
    public List<SubmissionGetListRequest> getSubmissionsList(String companyName, String password) {
        //회원 테이블에서 조회 먼저
        Member findMember = memberRepository.findByPortfolioMemberId(companyName).orElseThrow(() -> new BadCredentialsException("존재하지 않는 아이디이거나 비밀번호가 맞지습니다."));// 로그인 아이디랑 같음
        
        if (!passwordEncoder.matches(password, findMember.getPassword())) { // 비밀번호 확인
            throw new BadCredentialsException("존재하지 않는 아이디이거나 비밀번호가 맞지 않습니다.");
        }
        
        List<Submission> submissions = submissionRepository.findBySubmissions(findMember.getId());
        
        List<SubmissionGetListRequest> collect = submissions
                .stream()
                .map(item -> {
                    SubmissionGetListRequest submissionGetListRequest = new SubmissionGetListRequest(item.getId(), item.getPortfolio().getId(), item.getCompanyName(), item.getSubmissionJson(), item.getIsDraft(), item.getCompletedDate(), item.getModifyDate());
                    Portfolio portfolio = item.getPortfolio();
                    if (portfolio != null) {
                        submissionGetListRequest.setPortfolio(new SubmissionGetListRequest.Portfolio(portfolio.getTitle(), portfolio.getSlug()));
                    }
                    return submissionGetListRequest;
                }).collect(Collectors.toList());
        
        
        return collect;
    }
    
    @Override
    public SubmissionGetRequest getSubmissions(Long submissionId) {
        
        SubmissionGetRequest submission = submissionRepository.getSubmission(submissionId);
        
        List<CommonFile> fileList = commonFileRepository.findByFileTargetIdAndFileTypeList(submission.getSubmissionId(), CommonFileType.SUBMISSION_OPTION);
        
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(submission.getSubmissionJson(), new TypeReference<>() {
            });
            
            for (CommonFile file : fileList) {
                Map<String, Object> fileNode = new LinkedHashMap<>();
                fileNode.put("type", "file");
                fileNode.put("fileId", file.getId());
                fileNode.put("url", file.getFileUrl());
                fileNode.put("name", file.getFileName());
                fileNode.put("step", file.getQuestionStep());
                fileNode.put("order", file.getQuestionOrder());
                
                responseMap.put(
                        String.valueOf(file.getOptionsId()),
                        fileNode
                );
            }
            
            submission.setSubmissionJson(
                    objectMapper.writeValueAsString(responseMap)
            );
            
        } catch (Exception e) {
            throw new RuntimeException("submission json merge error", e);
        }
        
        return submission;
    }
    
    @Override
    public ApiResponse createSubmission(SubmissionPostRequest request) {
        Long memberId = request.getMemberId();
        Long submissionId = request.getSubmissionId();
        Long portfolioId = request.getPortfolioId();
        
        // 1. 필수 엔티티 조회
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포트폴리오입니다."));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        
        // 2. submission 조회
        Submission submission = Optional.ofNullable(submissionId)
                .flatMap(submissionRepository::findById)
                .orElse(null);
        
        // 3. 신규 생성 or 기존 수정
        if (submission == null) {
            submission = submissionRepository.save(
                    Submission.builder()
                            .portfolio(portfolio)
                            .member(member)
                            .submissionJson(request.getResponse())
                            .companyName(member.getLoginId())
                            .password(member.getPassword())
                            .isDraft(false)
                            .completedDate(LocalDateTime.now())
                            .build()
            );
        } else {
            submission.modifySubmission(request);
        }
        
        // 4. 기존 옵션 파일 전부 삭제
        List<CommonFile> oldFiles = commonFileRepository.findByFileTargetIdAndFileTypeList(submission.getId(), CommonFileType.SUBMISSION_OPTION);
        
        commonFileRepository.deleteSubmissionOptionFiles(submission.getId(), CommonFileType.SUBMISSION_OPTION);
        
        for (CommonFile file : oldFiles) {
            s3FileUtils.deleteFile(file.getFileUrl());
        }
        
        // 5. 옵션별 파일 재업로드
        for (SubmissionPostRequest.OptionFileRequest optionFile : request.getOptionFiles()) {
            
            Long optionsId = optionFile.getOptionsId();
            List<MultipartFile> files = optionFile.getFiles();
            
            if (files == null || files.isEmpty()) continue;
            
            for (MultipartFile file : files) {
                
                UploadResult upload = s3FileUtils.storeFile(file, "submission");
                
                CommonFile commonFile = CommonFile.builder()
                        .fileTargetId(submission.getId())
                        .optionsId(optionsId)
                        .questionStep(optionFile.getQuestionStep())
                        .questionOrder(optionFile.getQuestionOrder())
                        .fileName(upload.originalFilename())
                        .fileUuidName(upload.uuid())
                        .fileType(CommonFileType.SUBMISSION_OPTION)
                        .fileUrl(upload.url())
                        .build();
                
                commonFileRepository.save(commonFile);
            }
        }
        
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
        List<CommonFile> oldFiles = commonFileRepository.findByFileTargetIdAndFileTypeList(submission.getId(), CommonFileType.SUBMISSION_OPTION);
        // 옵션 단위 전부 delete → insert
        commonFileRepository.deleteSubmissionOptionFiles(
                submission.getId(),
                CommonFileType.SUBMISSION_OPTION
        );
        
        for (CommonFile file : oldFiles) {
            s3FileUtils.deleteFile(file.getFileUrl());
        }
        
        
        // 5.옵션별 파일 처리 (신규/기존 공통)
        for (SubmissionPostDraftRequest.OptionFileRequest optionFile : request.getOptionFiles()) {
            
            Long optionsId = optionFile.getOptionsId();
            List<MultipartFile> files = optionFile.getFiles();
            
            if (files.isEmpty()) continue;
            
            
            // 5-2. 새 파일 업로드 & 저장
            for (MultipartFile file : files) {
                
                UploadResult upload = s3FileUtils.storeFile(file, "submission");
                
                CommonFile commonFile = CommonFile.builder()
                        .fileTargetId(submission.getId())
                        .optionsId(optionsId)
                        .questionStep(optionFile.getQuestionStep())
                        .questionOrder(optionFile.getQuestionOrder())
                        .fileName(upload.originalFilename())
                        .fileUuidName(upload.uuid())
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
