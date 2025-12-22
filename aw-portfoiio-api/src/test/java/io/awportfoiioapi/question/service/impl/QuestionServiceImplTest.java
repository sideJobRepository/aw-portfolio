package io.awportfoiioapi.question.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.advice.exception.CustomException;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.question.dto.request.QuestionPostRequest;
import io.awportfoiioapi.question.dto.request.QuestionPutRequest;
import io.awportfoiioapi.question.dto.response.QuestionGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class QuestionServiceImplTest extends RepositoryAndServiceTestSupport {
    
    @DisplayName("질문 검증(단계)")
    @Test
    void test1() {
        
        QuestionPostRequest request = new QuestionPostRequest(
                4L,
                0,
                1,
                "질문 타이틀",
                "질문 설명",
                "SHORT_ANSWER",
                null,
                10,
                500,
                Boolean.TRUE,
                Boolean.TRUE,
                null
        );
        assertThatThrownBy(() ->
                questionService.createQuestion(request)
        )
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 존재 하는 포트폴리오 질문단계 입니다.");
    }
    
    @DisplayName("질문 검증(순서)")
    @Test
    void test2() {
        QuestionPostRequest request = new QuestionPostRequest(
                4L,
                0,
                1,
                "질문 타이틀",
                "질문 설명",
                "SHORT_ANSWER",
                null,
                10,
                500,
                Boolean.TRUE,
                Boolean.TRUE,
                null
        );
        
        assertThatThrownBy(() ->
                questionService.createQuestion(request)
        )
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 존재 하는 포트폴리오 질문단계 입니다.");
    }
    
    @DisplayName("질문 생성")
    @Test
    void test3() throws IOException {
        
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg", fis1
        );
        QuestionPostRequest request = new QuestionPostRequest(
                4L,
                1, //신규
                1,
                "질문 타이틀",
                "질문 설명",
                "SHORT_ANSWER",
                multipartFile1,
                10,
                500,
                Boolean.TRUE,
                Boolean.TRUE,
                null
        );
        ApiResponse question = questionService.createQuestion(request);
        System.out.println("question = " + question);
    }
    
    @DisplayName("질문 생성(같은 step이 있으니 order만 저장)")
    @Test
    void test4() throws IOException {
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg", fis1
        );
        QuestionPostRequest request = new QuestionPostRequest(
                7L,
                1,
                2,
                "질문 타이틀",
                "질문 설명",
                "SHORT_ANSWER",
                multipartFile1,
                10,
                500,
                Boolean.TRUE,
                Boolean.TRUE,
                null
        );
        ApiResponse question = questionService.createQuestion(request);
        System.out.println("question = " + question);
        
    }
    
    @DisplayName("질문 생성(안내사항저장)")
    @Test
    void test5() throws IOException {
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg", fis1
        );
        
        List<QuestionPostRequest.Notifications> notifications = new ArrayList<>();
        QuestionPostRequest.Notifications notifications1 = new QuestionPostRequest.Notifications("안내사항1");
        QuestionPostRequest.Notifications notifications2 = new QuestionPostRequest.Notifications("안내사항2");
        QuestionPostRequest.Notifications notifications3 = new QuestionPostRequest.Notifications("안내사항3");
        notifications.add(notifications1);
        notifications.add(notifications2);
        notifications.add(notifications3);
        QuestionPostRequest request = new QuestionPostRequest(
                7L,
                0,
                1,
                "질문 타이틀",
                "질문 설명",
                "SHORT_ANSWER",
                multipartFile1,
                10,
                500,
                Boolean.TRUE,
                Boolean.TRUE,
                notifications
        );
        ApiResponse question = questionService.createQuestion(request);
        System.out.println("question = " + question);
    }
    
    @DisplayName("질문 수정")
    @Test
    void test6() throws IOException {
        
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg", fis1
        );
        QuestionPutRequest request = new QuestionPutRequest();
        request.setOptionsId(5L);
        request.setStep(1);
        request.setOrder(3);
        request.setTitle("수정된 질문 제목");
        request.setDescription("수정된 질문 설명");
        request.setType("LONG_ANSWER");
        request.setMinLength(20);
        request.setMaxLength(300);
        request.setRequireMinLength(Boolean.TRUE);
        request.setIsRequired(Boolean.FALSE);
        
        // 썸네일 요청 (삭제만 테스트하고 싶으면 이거)
        QuestionPutRequest.ThumbnailRequest thumbnailRequest = new QuestionPutRequest.ThumbnailRequest(null, true);
        request.setThumbnail(thumbnailRequest);
        
        // 알림 수정 요청
        List<QuestionPutRequest.Notifications> notifications = new ArrayList<>();
        notifications.add(new QuestionPutRequest.Notifications(7L, "안내사항 수정23232323"));
        notifications.add(new QuestionPutRequest.Notifications(9L, "안내사항 저장"));
        notifications.add(new QuestionPutRequest.Notifications(10L, "안내사항 저장22"));
        notifications.add(new QuestionPutRequest.Notifications(null, "안내사항 gg"));
        request.setNotifications(notifications);
        
        // when
        ApiResponse response = questionService.modifyQuestion(request);
        
        // then
        System.out.println(response);
    }
    
    @DisplayName("질문 조회")
    @Test
    void test7(){
        List<QuestionGetResponse> question = questionService.getQuestion(7L);
        System.out.println("question = " + question);
    }
}