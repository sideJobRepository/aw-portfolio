package io.awportfoiioapi.submissions.serivce;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;

import java.util.List;

public interface SubmissionsService {
    
    List<SubmissionsGetRequest> getSubmissions();
    
    
    ApiResponse deleteSubmission(Long id);
}
