package io.awportfoiioapi.question.service.impl;

import io.awportfoiioapi.advice.exception.CategoryAndPortfolioException;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.notification.entity.Notification;
import io.awportfoiioapi.notification.respotiroy.NotificationRepository;
import io.awportfoiioapi.options.entity.Options;
import io.awportfoiioapi.options.enums.OptionsType;
import io.awportfoiioapi.options.respotiroy.OptionsRepository;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.portfolio.repository.PortfolioRepository;
import io.awportfoiioapi.question.dto.request.QuestionPostRequest;
import io.awportfoiioapi.question.dto.request.QuestionPutRequest;
import io.awportfoiioapi.question.dto.response.QuestionGetResponse;
import io.awportfoiioapi.question.dto.response.QuestionGetDetailResponse;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    
    private final QuestionRepository questionRepository;
    private final OptionsRepository optionsRepository;
    private final PortfolioRepository portfolioRepository;
    private final NotificationRepository notificationRepository;
    private final S3FileUtils s3FileUtils;
    
    @Override
    public List<QuestionGetResponse> getQuestion(Long portfolioId) {
       return questionRepository.findByQuestions(portfolioId);
    }
    
    @Override
    public QuestionGetDetailResponse getQuestionDetail(Long id) {
        return null;
    }
    
    @Override
    public ApiResponse createQuestion(QuestionPostRequest request) {
        Long portfolioId = request.getPortfolioId();
        Integer step = request.getStep();
        Integer order = request.getOrder();
        
        //같은 포트폴리오 같은 단계에 같은 순서가 있는지 확인
        Boolean orderResult = questionRepository.existsOrders(portfolioId, step, order);
        if (orderResult) {
            throw new CategoryAndPortfolioException("이미 존재 하는 포트폴리오 질문단계 순서 입니다.", "order");
        }
        // step 이 없으면 만들어주고 ,있으면 값을 찾기
        Question question = questionRepository.findByPortfolioStep(portfolioId, step);
        if (question == null) {
            Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new RuntimeException("존재 하지않는 포트폴리오 입니다."));// 없을리 없음
            Question saveQuestion = Question
                    .builder()
                    .portfolio(portfolio)
                    .step(step)
                    .build();
            question = questionRepository.save(saveQuestion);
        }
        OptionsType optionsType = OptionsType.valueOf(request.getType());
        
        String thumbnailUrl = null;
        
        MultipartFile thumbnail = request.getThumbnail();
        if (thumbnail != null && !thumbnail.isEmpty()) {
            UploadResult file = s3FileUtils.storeFile(thumbnail, "question");
            thumbnailUrl = file.url();
        }
        
        // options 만들어주기
        Options options = Options
                .builder()
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
                .build();
        
        Options saveOptions = optionsRepository.save(options);
        //안내사항 있는지 확인
        List<QuestionPostRequest.Notifications> notifications = request.getNotifications();
        if (notifications != null && !notifications.isEmpty()) {
            
            for (QuestionPostRequest.Notifications notification : notifications) {
                Notification SaveNotifications = Notification
                        .builder()
                        .options(saveOptions)
                        .description(notification.getValue())
                        .build();
                notificationRepository.save(SaveNotifications);
            }
        }
        return new ApiResponse(200, true, "질문이 생성되었습니다.");
    }
    
    @Override
    public ApiResponse modifyQuestion(QuestionPutRequest request) {
        Long optionsId = request.getOptionsId();
    
        // 1. Options 조회
        Options options = optionsRepository.findById(optionsId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 질문 옵션입니다."));
    
        Question question = options.getQuestion();
        Long portfolioId = question.getPortfolio().getId();
    
        Integer newStep = request.getStep();
        Integer newOrder = request.getOrder();
    
        // 2. step / order 변경 시 중복 검증
        if (!question.getStep().equals(newStep) || !options.getOrders().equals(newOrder)) {
            Boolean exists = questionRepository.existsOrders(
                    portfolioId,
                    newStep,
                    newOrder
            );
            if (exists) {
                throw new CategoryAndPortfolioException(
                        "이미 존재하는 질문 단계 순서입니다.", "order"
                );
            }
        }
    
        // 3. step 변경 시 Question 재매핑
        if (!question.getStep().equals(newStep)) {
            Question targetQuestion =
                    questionRepository.findByPortfolioStep(portfolioId, newStep);
    
            if (targetQuestion == null) {
                targetQuestion = questionRepository.save(
                        Question.builder()
                                .portfolio(question.getPortfolio())
                                .step(newStep)
                                .build()
                );
            }
            options.changeQuestion(targetQuestion); // 연관관계 변경
        }
    
        // 4. Options 기본 필드 수정
        options.updateBasic(
                newOrder,
                request.getTitle(),
                request.getDescription(),
                OptionsType.valueOf(request.getType()),
                request.getMinLength(),
                request.getMaxLength(),
                request.getRequireMinLength(),
                request.getIsRequired()
        );
    
        // 5. 썸네일 처리
        QuestionPutRequest.ThumbnailRequest thumbnailReq = request.getThumbnail();
    
        if (thumbnailReq != null) {
            // 삭제
            if (Boolean.TRUE.equals(thumbnailReq.getRemove())) {
                if (options.getThumbnail() != null) {
                    s3FileUtils.deleteFile(options.getThumbnail());
                    options.changeThumbnail(null);
                }
            }
    
            // 교체
            MultipartFile newFile = thumbnailReq.getFile();
            if (newFile != null && !newFile.isEmpty()) {
                if (options.getThumbnail() != null) {
                    s3FileUtils.deleteFile(options.getThumbnail());
                }
                UploadResult upload = s3FileUtils.storeFile(newFile, "question");
                options.changeThumbnail(upload.url());
            }
        }
    
        // 6.Notification 처리
        List<Notification> existingNotifications = notificationRepository.findByOptionsId(options.getId());
    
        Map<Long, Notification> notificationMap = existingNotifications.stream()
                .collect(Collectors.toMap(Notification::getId, n -> n));
    
        for (QuestionPutRequest.Notifications reqNoti : request.getNotifications()) {
    
            if (reqNoti.getId() == null) {
                // 신규 추가
                notificationRepository.save(
                        Notification.builder()
                                .options(options)
                                .description(reqNoti.getValue())
                                .build()
                );
            } else {
                // 수정
                Notification notification = notificationMap.remove(reqNoti.getId());
                if (notification != null) {
                    notification.changeDescription(reqNoti.getValue());
                }
            }
        }
    
        // 남은 기존 알림 → 삭제
        notificationRepository.deleteAll(notificationMap.values());
    
        return new ApiResponse(200, true, "질문이 수정되었습니다.");
    }
    
    @Override
    public ApiResponse deleteQuestion(Long ID) {
        return null;
    }
}
