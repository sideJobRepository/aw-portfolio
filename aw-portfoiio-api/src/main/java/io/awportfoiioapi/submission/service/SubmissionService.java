package io.awportfoiioapi.submission.service;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPostRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPutRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetListRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;

import java.util.List;

public interface SubmissionService {
    
    List<SubmissionGetListRequest> getSubmissionsList(String companyName,String password);
    
    SubmissionGetRequest getSubmissions(Long submissionId);
    
    ApiResponse createSubmission(SubmissionPostRequest request);
    
    ApiResponse temporaryStorage(SubmissionPostDraftRequest request); //임시저장
    
    ApiResponse modifySubmission(SubmissionPutRequest request);
    
    ApiResponse deleteSubmission(Long id);
}
