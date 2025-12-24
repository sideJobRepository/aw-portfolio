package io.awportfoiioapi.submissions.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.page.PageResponse;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import io.awportfoiioapi.submissions.serivce.SubmissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SubmissionsController {

    private final SubmissionsService submissionsService;
    
    @GetMapping("/admin-submissions")
    public PageResponse<SubmissionsGetRequest> getSubmissions(@PageableDefault(size = 10) Pageable pageable) {
        Page<SubmissionsGetRequest> submissions = submissionsService.getSubmissions(pageable);
        return PageResponse.from(submissions);
    }
    
    @DeleteMapping("/admin-submissions/{id}")
    public ApiResponse deleteSubmission(@PathVariable Long id) {
        return submissionsService.deleteSubmission(id);
    }
}
