package io.awportfoiioapi.submissions.serivce.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class SubmissionsServiceImplTest extends RepositoryAndServiceTestSupport {

    
    @DisplayName("제출목록 조회")
    @Test
    void test1(){
        
        List<SubmissionsGetRequest> submissions = submissionsService.getSubmissions();
        System.out.println("submissions = " + submissions);
    }
    
    @DisplayName("제출목록 삭제")
    @Test
    void test2(){
        ApiResponse apiResponse = submissionsService.deleteSubmission(10L);
        System.out.println("apiResponse = " + apiResponse);
    }
}