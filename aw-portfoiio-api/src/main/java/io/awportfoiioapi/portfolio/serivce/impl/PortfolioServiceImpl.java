package io.awportfoiioapi.portfolio.serivce.impl;

import io.awportfoiioapi.advice.exception.CategoryAndPortfolioException;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.category.entity.Category;
import io.awportfoiioapi.category.repository.CategoryRepository;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.file.repository.CommonFileRepository;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPostRequest;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPutRequest;
import io.awportfoiioapi.portfolio.dto.response.*;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.portfolio.repository.PortfolioRepository;
import io.awportfoiioapi.portfolio.serivce.PortfolioService;
import io.awportfoiioapi.utils.S3FileUtils;
import io.awportfoiioapi.utils.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CategoryRepository categoryRepository;
    private final CommonFileRepository commonFileRepository;
    private final S3FileUtils s3FileUtils;
    
    @Override
    public Page<PortfolioResponse> getPortfolioList(Pageable pageable,String name) {
        
        Page<PortfolioResponse> portfolioList = portfolioRepository.getPortfolioList(pageable, name);
        
        List<PortfolioQuestionCountResponse> byQuestionCount = portfolioRepository.findByQuestionCount();
        List<PortfolioSubmissionCountResponse> bySubmissionCount = portfolioRepository.findBySubmissionCount();
        
        Map<Long, Long> questionCountMap = byQuestionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioQuestionCountResponse::getPortfolioId,
                        PortfolioQuestionCountResponse::getCount
                ));
        
        Map<Long, Long> submissionCountMap = bySubmissionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioSubmissionCountResponse::getPortfolioId,
                        PortfolioSubmissionCountResponse::getCount
                ));
        
        for (PortfolioResponse portfolio : portfolioList) {
            
            // ---------- 1) 썸네일 presigned 변환 ----------
            String thumbnailUrl = portfolio.getThumbnail();
            
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                // 기존 URL → key 뽑기
                String key = s3FileUtils.getFileNameFromUrl(thumbnailUrl);
                
                // presigned 생성
                String presigned = s3FileUtils.createPresignedUrl(key);
                
                // DTO에 다시 넣기
                portfolio.setThumbnail(presigned);
            }
            
            // ---------- 2) 질문 수 ----------
            Long qCount = questionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setQuestions(qCount);
            
            // ---------- 3) 제출 수 ----------
            Long sCount = submissionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setSubmissions(sCount);
        }
        
        return portfolioList;
    }
    
    @Override
    public List<PortfolioResponse> getPortfolioList() {
        List<PortfolioResponse> portfolioList = portfolioRepository.getPortfolioList();
        List<PortfolioQuestionCountResponse> byQuestionCount = portfolioRepository.findByQuestionCount();
        List<PortfolioSubmissionCountResponse> bySubmissionCount = portfolioRepository.findBySubmissionCount();
        
        Map<Long, Long> questionCountMap = byQuestionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioQuestionCountResponse::getPortfolioId,
                        PortfolioQuestionCountResponse::getCount
                ));
        
        Map<Long, Long> submissionCountMap = bySubmissionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioSubmissionCountResponse::getPortfolioId,
                        PortfolioSubmissionCountResponse::getCount
                ));
        
        for (PortfolioResponse portfolio : portfolioList) {
            
            // ---------- 1) 썸네일 presigned 변환 ----------
            String thumbnailUrl = portfolio.getThumbnail();
            
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                // 기존 URL → key 뽑기
                String key = s3FileUtils.getFileNameFromUrl(thumbnailUrl);
                
                // presigned 생성
                String presigned = s3FileUtils.createPresignedUrl(key);
                
                // DTO에 다시 넣기
                portfolio.setThumbnail(presigned);
            }
            
            // ---------- 2) 질문 수 ----------
            Long qCount = questionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setQuestions(qCount);
            
            // ---------- 3) 제출 수 ----------
            Long sCount = submissionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setSubmissions(sCount);
        }
        
        return portfolioList;
    }
    
    @Override
    public PortfolioGetDetailResponse getPortfolioDetail(Long id) {
        return portfolioRepository.findByPortfolioDetail(id);
    }
    
    @Override
    public List<PortfolioResponse> getPortfolioList(Boolean active, Long categoryId) {
        List<PortfolioResponse> portfolioList = portfolioRepository.getPortfolioList(active, categoryId);
        List<PortfolioQuestionCountResponse> byQuestionCount = portfolioRepository.findByQuestionCount();
        List<PortfolioSubmissionCountResponse> bySubmissionCount = portfolioRepository.findBySubmissionCount();
        
        Map<Long, Long> questionCountMap = byQuestionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioQuestionCountResponse::getPortfolioId,
                        PortfolioQuestionCountResponse::getCount
                ));
        
        Map<Long, Long> submissionCountMap = bySubmissionCount.stream()
                .collect(Collectors.toMap(
                        PortfolioSubmissionCountResponse::getPortfolioId,
                        PortfolioSubmissionCountResponse::getCount
                ));
        
        for (PortfolioResponse portfolio : portfolioList) {
            
            // ---------- 1) 썸네일 presigned 변환 ----------
            String thumbnailUrl = portfolio.getThumbnail();
            
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                // 기존 URL → key 뽑기
                String key = s3FileUtils.getFileNameFromUrl(thumbnailUrl);
                
                // presigned 생성
                String presigned = s3FileUtils.createPresignedUrl(key);
                
                // DTO에 다시 넣기
                portfolio.setThumbnail(presigned);
            }
            
            // ---------- 2) 질문 수 ----------
            Long qCount = questionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setQuestions(qCount);
            
            // ---------- 3) 제출 수 ----------
            Long sCount = submissionCountMap.getOrDefault(portfolio.getId(), 0L);
            portfolio.getCount().setSubmissions(sCount);
        }
        
        return portfolioList;
    }
    
    @Override
    public List<PortfoliosGetDetailResponse> getPortfolioDetailOptions(Long id) {
        return portfolioRepository.getPortfolioDetailOptions(id);
    }
    
    @Override
    public PortfoliosOneGetResponse getPortfolioOneDetail(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.getPortfolio(portfolioId);
        
        PortfoliosOneGetResponse portfoliosOneGetResponse = new PortfoliosOneGetResponse(portfolio);
        List<PortfolioQuestionCountResponse> byQuestionCount = portfolioRepository.findByQuestionCount();
        List<PortfolioSubmissionCountResponse> bySubmissionCount = portfolioRepository.findBySubmissionCount();
        Map<Long, Long> questionCountMap =
                byQuestionCount.stream()
                        .collect(Collectors.toMap(
                                PortfolioQuestionCountResponse::getPortfolioId,
                                PortfolioQuestionCountResponse::getCount
                        ));
        
        Map<Long, Long> submissionCountMap =
                bySubmissionCount.stream()
                             .collect(Collectors.toMap(
                                     PortfolioSubmissionCountResponse::getPortfolioId,
                                  PortfolioSubmissionCountResponse::getCount
                             ));
       
    
        // 3. 단일 portfolio에 질문 수 세팅
        String thumbnail = portfoliosOneGetResponse.getThumbnail();
        if (StringUtils.hasText(thumbnail)) {
            String fileNameFromUrl = s3FileUtils.getFileNameFromUrl(thumbnail);
            String presignedUrl = s3FileUtils.createPresignedUrl(fileNameFromUrl);
            portfoliosOneGetResponse.setThumbnail(presignedUrl);
        }
        Long questionCount = questionCountMap.getOrDefault(portfolioId, 0L);
        Long submissionCount = submissionCountMap.getOrDefault(portfolioId, 0L);
    
        portfoliosOneGetResponse.getCount().setQuestions(questionCount);
        portfoliosOneGetResponse.getCount().setSubmissions(submissionCount);
    
        return portfoliosOneGetResponse;
    }
    
    @Override
    public ApiResponse createPortfolio(PortfolioPostRequest request) {
        Integer order = request.getOrder();
        boolean result = portfolioRepository.existsByPortfolioOrder(order);
        if (result) {
            throw new CategoryAndPortfolioException("이미 존재 하는 포트폴리오 순서입니다.", "order");
        }
        // 파일
        MultipartFile thumbnail = request.getThumbnail();
        Long requestCategoryId = request.getCategoryId();
        
        // category는 null 허용
        Category category = null;
        if (requestCategoryId != null) {
            category = categoryRepository.findById(requestCategoryId).orElse(null);
        }

        // 썸네일 업로드 (있으면)
        UploadResult uploadResult = null;
        String thumbnailUrl = null;
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            uploadResult = s3FileUtils.storeFile(thumbnail, "portfolio");
            thumbnailUrl = uploadResult.url();
        }

        // Portfolio는 항상 생성
        Portfolio portfolio = Portfolio.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .slug(request.getSlug())
                .domain(request.getDomain())
                .thumbnail(thumbnailUrl) // null 가능
                .orders(request.getOrder())
                .isActive(request.getIsActive())
                .build();
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

        // 파일이 있을 때만 CommonFile 생성
        if (uploadResult != null) {
            CommonFile commonFile = CommonFile.builder()
                    .fileTargetId(savedPortfolio.getId())
                    .fileName(uploadResult.originalFilename())
                    .fileUuidName(uploadResult.uuid())
                    .fileUrl(uploadResult.url())
                    .fileType(CommonFileType.PORTFOLIO)
                    .build();
            
            commonFileRepository.save(commonFile);
        }
        
        return new ApiResponse(200,true,"포트폴리오가 생성되었습니다.");
    }
    
    @Override
    public ApiResponse modifyPortfolio(PortfolioPutRequest request) {
        
        Long id = request.getId();
        // 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포트폴리오 입니다."));
        
        // order 중복 체크 (자기 자신 제외)
        Integer newOrder = request.getOrder();
        Integer currentOrder = portfolio.getOrders();
        
        if (!Objects.equals(currentOrder, newOrder)) {
            boolean exists = portfolioRepository.existsByPortfolioOrder(newOrder, portfolio.getId());
            
            if (exists) {
                throw new CategoryAndPortfolioException(
                        "이미 존재하는 포트폴리오 순서입니다.",
                        "order"
                );
            }
        }
        //일반 필드 업데이트
        portfolio.update(request);
        
        //카테고리 (null 허용)
        if (request.getCategoryId() != null) {
            Category category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElse(null);
            portfolio.updateCategory(category);
        } else {
            portfolio.updateCategory(null);
        }
        
        // 썸네일 수정 여부
        PortfolioPutRequest.ThumbnailRequest thumbnail = request.getThumbnail();
        
        if (thumbnail != null) {
            
            // 5-1 썸네일 삭제
            if (thumbnail.getRemove()) {
                deleteThumbnail(portfolio);
            }
            // 5-2 썸네일 교체
            if (thumbnail.getFile() != null && !thumbnail.getFile().isEmpty()) {
                replaceThumbnail(portfolio, thumbnail.getFile());
            }
        }
        
        return new ApiResponse(200, true, "포트폴리오가 수정되었습니다.");
    }
    
    @Override
    public ApiResponse deletePortfolio(Long id) {
        // 포트폴리오 조회
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 포트폴리오 입니다."));
        
        // 연결된 파일 조회
        CommonFile file = commonFileRepository
                .findByFileTargetIdAndFileType(
                        portfolio.getId(),
                        CommonFileType.PORTFOLIO
                );
        
        // 파일 있으면 삭제
        if (file != null) {
            s3FileUtils.deleteFile(file.getFileUrl());
            commonFileRepository.delete(file);
        }
        
        // 포트폴리오 삭제
        portfolioRepository.delete(portfolio);
        
        return new ApiResponse(200, true, "포트폴리오가 삭제되었습니다.");
    }
    
    private void deleteThumbnail(Portfolio portfolio) {
        CommonFile oldFile = commonFileRepository
                .findByPortfolioFile(
                        portfolio.getId(),
                        CommonFileType.PORTFOLIO
                );
        
        if (oldFile != null) {
            s3FileUtils.deleteFile(oldFile.getFileUrl());
            commonFileRepository.delete(oldFile);
        }
        
        portfolio.updateThumbnail(null);
    }
    
    private void replaceThumbnail(Portfolio portfolio, MultipartFile thumbnail) {
        // 기존 파일 정리
        deleteThumbnail(portfolio);
        
        // 새 파일 업로드
        UploadResult uploadResult = s3FileUtils.storeFile(thumbnail, "portfolio");
        
        // 엔티티 업데이트
        portfolio.updateThumbnail(uploadResult.url());
        
        // CommonFile 저장
        CommonFile newFile = CommonFile.builder()
                .fileTargetId(portfolio.getId())
                .fileName(uploadResult.originalFilename())
                .fileUuidName(uploadResult.uuid())
                .fileUrl(uploadResult.url())
                .fileType(CommonFileType.PORTFOLIO)
                .build();
        
        commonFileRepository.save(newFile);
    }
}
