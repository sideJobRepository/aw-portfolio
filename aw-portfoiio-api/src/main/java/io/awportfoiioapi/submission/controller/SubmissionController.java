package io.awportfoiioapi.submission.controller;


import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.service.SubmissionService;
import io.awportfoiioapi.utils.JwtParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SubmissionController {

    private final SubmissionService submissionService;
    
    //임시저장
    @PostMapping("/submission/temporaryStorage")
    public ApiResponse temporaryStorage(@AuthenticationPrincipal Jwt jwt , @RequestBody SubmissionPostDraftRequest request) {
        Long memberId = JwtParserUtil.extractMemberId(jwt);
        request.setMemberId(memberId);
        return submissionService.temporaryStorage(request);
    }
    
    
}
