package io.awportfoiioapi.submissions.serivce;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionsService {
    
    Page<SubmissionsGetRequest> getSubmissions(Pageable pageable);
    
    
    ApiResponse deleteSubmission(Long id);
}
