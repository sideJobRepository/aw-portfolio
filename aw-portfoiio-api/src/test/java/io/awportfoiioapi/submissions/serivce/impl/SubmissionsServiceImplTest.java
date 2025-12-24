package io.awportfoiioapi.submissions.serivce.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionsServiceImplTest extends RepositoryAndServiceTestSupport {

    
    @DisplayName("제출목록 조회")
    @Test
    void test1(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<SubmissionsGetRequest> submissions = submissionsService.getSubmissions(pageRequest);
        System.out.println("submissions = " + submissions);
    }
    
    @DisplayName("제출목록 삭제")
    @Test
    void test2(){
        ApiResponse apiResponse = submissionsService.deleteSubmission(10L);
        System.out.println("apiResponse = " + apiResponse);
    }
}