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
import java.util.*;
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
    
    
    @Override
    public List<SubmissionGetListRequest> getSubmissionsList(Long memberId) {
        //회원 테이블에서 조회 먼저
        
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        
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
                
                // 1) 기존 URL
                String originalUrl = file.getFileUrl();
                
                // 2) key 추출
                String key = s3FileUtils.getFileNameFromUrl(originalUrl);
                
                // 3) presigned URL 생성
                String presignedUrl = s3FileUtils.createPresignedUrl(key);
                
                Map<String, Object> fileNode = new LinkedHashMap<>();
                fileNode.put("type", "file");
                fileNode.put("fileId", file.getId());
                fileNode.put("url", presignedUrl);
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
            // JSON 수정 + 완료처리
            submission.modifySubmission(request);
        }
        
        /**
         * optionFiles 자체가 없으면 파일은 건드리지 않음
         * -> 다른 질문 파일 삭제되는 문제 방지
         */
        if (request.getOptionFiles() == null || request.getOptionFiles().isEmpty()) {
            return new ApiResponse(200, true, "제출이 완료되었습니다.", submission.getId());
        }
        
        /**
         * DB에 저장된 기존 파일 전체 조회
         */
        List<CommonFile> existingFiles = commonFileRepository.findByFileTargetIdAndFileTypeList(submission.getId(), CommonFileType.SUBMISSION_OPTION);
        
        /**
         * 요청에서 삭제할 파일 id 목록 수집
         */
        Set<Long> deleteFileIds = request.getOptionFiles().stream()
                        .map(SubmissionPostRequest.OptionFileRequest::getDeleteFileId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        
        // 매칭되는 파일 전체 삭제
        for (CommonFile file : existingFiles) {
            if (deleteFileIds.contains(file.getId())) {
                s3FileUtils.deleteFile(file.getFileUrl());
                commonFileRepository.delete(file);
            }
        }
        
        /**
         * 신규 업로드 처리 (해당 optionId 내부)
         */
        for (SubmissionPostRequest.OptionFileRequest optionFile : request.getOptionFiles()) {
            
            Long optionId = optionFile.getOptionsId();
            List<MultipartFile> files = optionFile.getFiles();
            
            if (files == null || files.isEmpty()) continue;
            
            for (MultipartFile file : files) {
                
                UploadResult upload = s3FileUtils.storeFile(file, "submission");
                
                CommonFile commonFile = CommonFile.builder()
                        .fileTargetId(submission.getId())
                        .optionsId(optionId)
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
        
        return new ApiResponse(200, true, "제출이 완료되었습니다.", submission.getId());
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
        
        /**
         * optionFiles 자체가 없으면 파일은 건드리지 않고 종료
         *  - 다른 질문 영역 파일 삭제 방지
         */
        if (request.getOptionFiles() == null || request.getOptionFiles().isEmpty()) {
            return new ApiResponse(200, true, "임시저장 되었습니다.", submission.getId());
        }
        
        request.getOptionFiles()
               .stream().filter(item -> item.getDeleteFileId() == null)
                .forEach(item -> {
                    Long optionsId = item.getOptionsId();
                    Integer questionStep = item.getQuestionStep();
                    Integer questionOrder = item.getQuestionOrder();
                    CommonFile byDeleteFile = commonFileRepository.findByDeleteFile(optionsId, questionStep, questionOrder);
                    if (byDeleteFile == null) {
                        return;
                    }
                    s3FileUtils.deleteFile(byDeleteFile.getFileUrl());
                    commonFileRepository.delete(byDeleteFile);
                });
        
        
        /**
         * 5. DB에 있는 모든 파일 조회 (SUBMISSION_OPTION 전체)
         */
        List<CommonFile> existingFiles =
                commonFileRepository.findByFileTargetIdAndFileTypeList(
                        submission.getId(),
                        CommonFileType.SUBMISSION_OPTION
                );
        
        
        /**
         * 요청에서 삭제할 파일 id 목록 수집
         */
        Set<Long> deleteFileIds =
                request.getOptionFiles().stream()
                        .map(SubmissionPostDraftRequest.OptionFileRequest::getDeleteFileId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        
        // 매칭되는 파일 전체 삭제
        for (CommonFile file : existingFiles) {
            if (deleteFileIds.contains(file.getId())) {
                s3FileUtils.deleteFile(file.getFileUrl());
                commonFileRepository.delete(file);
            }
        }
        /**
         * 9. 신규 업로드 처리
         *  - 요청 optionId 범위 안에서만 새 파일 추가
         */
        for (SubmissionPostDraftRequest.OptionFileRequest optionFile : request.getOptionFiles()) {
            
            Long optionId = optionFile.getOptionsId();
            List<MultipartFile> files = optionFile.getFiles();
            
            if (files == null || files.isEmpty()) continue;
            
            for (MultipartFile file : files) {
                
                UploadResult upload = s3FileUtils.storeFile(file, "submission");
                
                CommonFile commonFile = CommonFile.builder()
                        .fileTargetId(submission.getId())
                        .optionsId(optionId)
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
}
