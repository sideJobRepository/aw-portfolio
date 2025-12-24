package io.awportfoiioapi.submission.controller;


import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPostRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetListRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.service.SubmissionService;
import io.awportfoiioapi.utils.JwtParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SubmissionController {

    private final SubmissionService submissionService;
    
    
    @GetMapping("/submission/{id}")
    public SubmissionGetRequest getSubmission(@PathVariable Long id) {
        return submissionService.getSubmissions(id);
    }
    
    //비밀번호 때문에 조회라도 post 로 요청
    @PostMapping("/submission/my-list")
    public List<SubmissionGetListRequest> getMySubmissions(@RequestParam(name = "companyName") String companyName, @RequestParam(name = "password") String password) {
        return submissionService.getSubmissionsList(companyName,password);
    }
    //임시저장
    @PostMapping("/submission/temporaryStorage")
    public ApiResponse temporaryStorage(@AuthenticationPrincipal Jwt jwt, @RequestBody SubmissionPostDraftRequest request) {
        Long memberId = JwtParserUtil.extractMemberId(jwt);
        request.setMemberId(memberId);
        return submissionService.temporaryStorage(request);
    }
    @PostMapping
    public ApiResponse createSubmission(@AuthenticationPrincipal Jwt jwt , @RequestBody SubmissionPostRequest request) {
        Long memberId = JwtParserUtil.extractMemberId(jwt);
        request.setMemberId(memberId);
        return submissionService.createSubmission(request);
    }
    
    
}
