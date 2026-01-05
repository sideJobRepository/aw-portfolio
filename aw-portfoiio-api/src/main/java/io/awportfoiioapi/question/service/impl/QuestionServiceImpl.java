package io.awportfoiioapi.question.service.impl;

import io.awportfoiioapi.advice.exception.CategoryAndPortfolioException;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.file.repository.CommonFileRepository;
import io.awportfoiioapi.options.entity.Options;
import io.awportfoiioapi.options.enums.OptionsType;
import io.awportfoiioapi.options.respotiroy.OptionsRepository;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.portfolio.repository.PortfolioRepository;
import io.awportfoiioapi.question.dto.request.QuestionPostRequest;
import io.awportfoiioapi.question.dto.request.QuestionPutRequest;
import io.awportfoiioapi.question.dto.response.QuestionGetResponse;
import io.awportfoiioapi.question.entity.Question;
import io.awportfoiioapi.question.respotiroy.QuestionRepository;
import io.awportfoiioapi.question.service.QuestionService;
import io.awportfoiioapi.utils.S3FileUtils;
import io.awportfoiioapi.utils.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionRepository questionRepository;
    private final OptionsRepository optionsRepository;
    private final PortfolioRepository portfolioRepository;
    private final CommonFileRepository commonFileRepository;
    private final S3FileUtils s3FileUtils;
    
    @Override
    public List<QuestionGetResponse> getQuestion(Long portfolioId) {
        
        List<QuestionGetResponse> questions = questionRepository.findByQuestions(portfolioId);
        
        for (QuestionGetResponse question : questions) {
            String thumbnail = question.getThumbnail();
            // 기존 URL → key 뽑기
            String key = s3FileUtils.getFileNameFromUrl(thumbnail);
            
            // presigned 생성
            String presigned = s3FileUtils.createPresignedUrl(key);
            question.setThumbnail(presigned);
        }
        return questions;
    }
    
    
    @Override
    public ApiResponse createQuestion(QuestionPostRequest request) {
        Long portfolioId = request.getPortfolioId();
        Integer step = request.getStep();
        Integer order = request.getOrder();
        
        // 1. 같은 포트폴리오 + 같은 step + 같은 order 검증
        if (questionRepository.existsOrders(portfolioId, step, order)) {
            throw new CategoryAndPortfolioException(
                    "이미 존재 하는 포트폴리오 질문단계 순서 입니다.", "order"
            );
        }
        
        // 2. Question 조회 또는 생성
        Question question = questionRepository.findByPortfolioStep(portfolioId, step);
        if (question == null) {
            Portfolio portfolio = portfolioRepository.findById(portfolioId)
                    .orElseThrow(() -> new RuntimeException("존재 하지않는 포트폴리오 입니다."));
            
            question = questionRepository.save(
                    Question.builder()
                            .portfolio(portfolio)
                            .step(step)
                            .build()
            );
        }
        
        // 3. OptionsType
        OptionsType optionsType = OptionsType.valueOf(request.getType());
        
        // 4. 썸네일 업로드 (있을 경우)
        String thumbnailUrl = null;
        MultipartFile thumbnail = request.getThumbnail();
        UploadResult uploadResult = null;
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            uploadResult = s3FileUtils.storeFile(thumbnail, "options");
            thumbnailUrl = uploadResult.url();
        }
        
        // 5. Options 생성
        Options options = Options.builder()
                .question(question)
                .orders(order)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(optionsType)
                .thumbnail(thumbnailUrl)
                .minLength(request.getMinLength())
                .maxLength(request.getMaxLength())
                .minLengthIsActive(request.getRequireMinLength())
                .optionsIsActive(request.getIsRequired())
                .option(request.getOptions())
                .build();
        
        Options savedOptions = optionsRepository.save(options);
        
        // 6. CommonFile 저장 (썸네일이 있을 때만)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            CommonFile commonFile = CommonFile.builder()
                    .fileName(thumbnail.getOriginalFilename())
                    .fileUrl(thumbnailUrl)
                    .fileTargetId(savedOptions.getId())
                    .fileType(CommonFileType.OPTIONS)
                    .fileUuidName(uploadResult.uuid())
                    .build();
            
            commonFileRepository.save(commonFile);
        }
        
        return new ApiResponse(200, true, "질문이 생성되었습니다.");
    }
    
    @Override
    public ApiResponse modifyQuestion(QuestionPutRequest request) {
        Long optionsId = request.getOptionsId();
        
        Options options = optionsRepository.findById(optionsId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 질문 옵션입니다."));
        
        Question question = options.getQuestion();
        Long portfolioId = question.getPortfolio().getId();
        
        Integer newStep = request.getStep();
        Integer newOrder = request.getOrder();
        
        // 순서 변경 시 중복 검증
        if (!question.getStep().equals(newStep) || !options.getOrders().equals(newOrder)) {
            if (questionRepository.existsOrders(portfolioId, newStep, newOrder)) {
                throw new CategoryAndPortfolioException("이미 존재하는 질문 단계 순서입니다.", "order");}
        }
        
        // step 변경
        if (!question.getStep().equals(newStep)) {
            Question target = questionRepository.findByPortfolioStep(portfolioId, newStep);
            if (target == null) {
                target = questionRepository.save(
                        Question.builder()
                                .portfolio(question.getPortfolio())
                                .step(newStep)
                                .build()
                );
            }
            options.changeQuestion(target);
        }
        
        // 기본 필드 수정
        options.updateBasic(
                newOrder,
                request.getTitle(),
                request.getDescription(),
                OptionsType.valueOf(request.getType()),
                request.getMinLength(),
                request.getMaxLength(),
                request.getRequireMinLength(),
                request.getIsRequired(),
                request.getOptions()
        );
        
        // ======================
        // 썸네일 처리 (핵심)
        // ======================
        QuestionPutRequest.ThumbnailRequest thumbnailReq = request.getThumbnail();
        
        if (thumbnailReq != null) {
        
            // 삭제 요청
            if (Boolean.TRUE.equals(thumbnailReq.getRemove())) {
        
                // 1. S3 파일 삭제 (URL 있을 때만)
                if (options.getThumbnail() != null) {
                    s3FileUtils.deleteFile(options.getThumbnail());
                }
        
                // 2. CommonFile 메타데이터 삭제 (무조건 시도)
                commonFileRepository.deleteByTargetIdAndType(
                        options.getId(), CommonFileType.OPTIONS
                );
        
                // 3. 옵션 썸네일 null 처리
                options.changeThumbnail(null);
            }
        
            // 교체 요청
            MultipartFile newFile = thumbnailReq.getFile();
            if (newFile != null && !newFile.isEmpty()) {
        
                // 1. 기존 S3 파일 삭제
                if (options.getThumbnail() != null) {
                    s3FileUtils.deleteFile(options.getThumbnail());
                }
        
                // 2. 기존 CommonFile 삭제 (항상)
                commonFileRepository.deleteByTargetIdAndType(
                        options.getId(), CommonFileType.OPTIONS
                );
        
                // 3. 새 파일 업로드
                UploadResult upload = s3FileUtils.storeFile(newFile, "options");
        
                // 4. 옵션 업데이트
                options.changeThumbnail(upload.url());
        
                // 5. CommonFile 저장
                commonFileRepository.save(
                        CommonFile.builder()
                                .fileTargetId(options.getId())
                                .fileUuidName(upload.uuid())
                                .fileName(upload.originalFilename())
                                .fileType(CommonFileType.OPTIONS)
                                .fileUrl(upload.url())
                                .build()
                );
            }
        }
        
        return new ApiResponse(200, true, "질문이 수정되었습니다.");
    }
    
    @Override
    public ApiResponse deleteQuestion(Long optionsId) {
        
        
        // 1. Options 조회
        Options options = optionsRepository.findById(optionsId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 옵션입니다."));
    
        Question question = options.getQuestion();
        Long questionId = question.getId();
    
        // 3. 썸네일 파일 삭제 (S3)
        if (options.getThumbnail() != null) {
            s3FileUtils.deleteFile(options.getThumbnail());
        }
    
        // 4. CommonFile 삭제 (옵션 썸네일)
        commonFileRepository.deleteByTargetIdAndType(optionsId, CommonFileType.OPTIONS);
    
        // 5. Options 삭제
        optionsRepository.delete(options);
    
        // 6. 남은 Options 개수 확인
        Long remainCount = optionsRepository.countByQuestionId(questionId);
    
        // 7. Options 없으면 Question 삭제
        if (remainCount == 0) {
            questionRepository.delete(question);
        }
    
        return new ApiResponse(200, true, "질문이 삭제되었습니다.");
    }
}
