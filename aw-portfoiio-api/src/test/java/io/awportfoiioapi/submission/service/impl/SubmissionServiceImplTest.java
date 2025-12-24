package io.awportfoiioapi.submission.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPostRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetListRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
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
    //    이건 모자가 아니잖아.jpg
      //  참새작.png
        File file = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");

        FileInputStream fis = new FileInputStream(file);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "files",                 // 중요
                file.getName(),
                "image/jpeg",
                fis
        );
        
        List<MultipartFile> files = Arrays.asList(multipartFile);
        
        SubmissionPostDraftRequest.OptionFileRequest optionFileRequest =
                new SubmissionPostDraftRequest.OptionFileRequest(20L,1,1, files);
    
        SubmissionPostDraftRequest request =
                new SubmissionPostDraftRequest(
                        null,
                        2L,
                        7L,
                        "  \"18\": \"111\",\n" +
                                "  \"19\": \"1111\",\n" +
                                "  \"21\": \"11111\",\n" +
                                "  \"rooms\": [\n" +
                                "    {\n" +
                                "      \"id\": \"room-1\",\n" +
                                "      \"name\": \"\",\n" +
                                "      \"desc\": \"\",\n" +
                                "      \"type\": \"\",\n" +
                                "      \"price\": \"\"\n" +
                                "    }\n" +
                                "  ],\n" +
                                "  \"specials\": [\n" +
                                "    {\n" +
                                "      \"id\": \"special-1\",\n" +
                                "      \"name\": \"\",\n" +
                                "      \"desc\": \"\"\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}",
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
    
    @DisplayName("작성폼 상세조회")
    @Test
    void test3(){
        SubmissionGetRequest submissions = submissionService.getSubmissions(8L);
        System.out.println("submissions = " +  submissions);
    }
    @DisplayName("작성폼 제출")
    @Test
    void test4() throws IOException {
           //    이건 모자가 아니잖아.jpg
            //  참새작.png
              File file = new File("src/test/java/io/awportfoiioapi/image/참새작.png");
      
              FileInputStream fis = new FileInputStream(file);
        
              MockMultipartFile multipartFile = new MockMultipartFile(
                      "files",                 // 중요
                      file.getName(),
                      "image/jpeg",
                      fis
              );
              
              List<MultipartFile> files = Arrays.asList(multipartFile);
              
              SubmissionPostRequest.OptionFileRequest optionFileRequest = new SubmissionPostRequest.OptionFileRequest(20L,1,1, files);
        
              
              
        SubmissionPostRequest request =
                new SubmissionPostRequest(
                        9L,
                        2L,
                        7L,
                                "\"21\": \"11111\",\n" +
                                "\"rooms\": [\n" +
                                "  {\n" +
                                "    \"id\": \"room-1\",\n" +
                                "    \"name\": \"\",\n" +
                                "    \"desc\": \"\",\n" +
                                "    \"type\": \"\",\n" +
                                "    \"price\": \"\"\n" +
                                "  }\n" +
                                "],\n" +
                                "\"specials\": [\n" +
                                "  {\n" +
                                "    \"id\": \"special-1\",\n" +
                                "    \"name\": \"\",\n" +
                                "    \"desc\": \"\"\n" +
                                "  }\n" +
                                "]\n" +
                                "}",
                        List.of(optionFileRequest)
                );
    
        ApiResponse response = submissionService.createSubmission(request);
    
        System.out.println("response = " + response);
    
    }
    
    @DisplayName("작성 포트폴리오 조회")
    @Test
    void test5(){
        
        List<SubmissionGetListRequest> test = submissionService.getSubmissionsList("test", "1234");
        
        
        System.out.println("test = " + test);
    }
}