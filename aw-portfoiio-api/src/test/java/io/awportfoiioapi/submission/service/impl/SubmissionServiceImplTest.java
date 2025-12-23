package io.awportfoiioapi.submission.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class SubmissionServiceImplTest extends RepositoryAndServiceTestSupport {
    
    
    @DisplayName("작성폼 임시저장")
    @Test
    void test1() throws IOException {
        File file = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
    
        FileInputStream fis = new FileInputStream(file);
    
        MockMultipartFile multipartFile = new MockMultipartFile(
                "files",                 // 중요
                file.getName(),
                "image/jpeg",
                fis
        );
    
        List<MultipartFile> files = List.of(multipartFile);
    
        SubmissionPostDraftRequest.OptionFileRequest optionFileRequest =
                new SubmissionPostDraftRequest.OptionFileRequest(6L,1,3, files);
    
        SubmissionPostDraftRequest request =
                new SubmissionPostDraftRequest(
                        null,
                        2L,
                        7L,
                        "{test}",
                        List.of(optionFileRequest)
                );
    
        ApiResponse response = submissionService.temporaryStorage(request);
    
        System.out.println("response = " + response);
    }
    
    @DisplayName("작성폼 임시저장(이미있는작성폼일경우)")
    @Test
    void test2() {
        //SubmissionPostDraftRequest request = new SubmissionPostDraftRequest(2L, 2L, 7L, "{ㄷㅇㅇㅇㅇㅇㅇㅇㅇ}");
        
//        ApiResponse apiResponse = submissionService.temporaryStorage(request);
//        System.out.println("apiResponse = " + apiResponse);
        
    }
}