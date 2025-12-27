package io.awportfoiioapi.submissions.controller;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import io.awportfoiioapi.submissions.serivce.SubmissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SubmissionsController {
    
    private final SubmissionsService submissionsService;
    
    @GetMapping("/admin-submissions")
    public List<SubmissionsGetRequest> getSubmissions() {
        return submissionsService.getSubmissions();
        
    }
    
    @DeleteMapping("/admin-submissions/{id}")
    public ApiResponse deleteSubmission(@PathVariable Long id) {
        return submissionsService.deleteSubmission(id);
    }
}
