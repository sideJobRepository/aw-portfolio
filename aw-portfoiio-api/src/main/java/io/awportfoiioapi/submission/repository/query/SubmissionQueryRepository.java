package io.awportfoiioapi.submission.repository.query;

import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmissionQueryRepository {
    SubmissionGetRequest getSubmission(Long submissionId);
    
    List<Submission> findBySubmissions(Long memberId);
    
    List<Long> findBySubmissionIds(Long id);
    
    void deleteByMemberSubmissions(Long id);
    void deleteBySubmissions(Long id);
    
    Page<SubmissionsGetRequest> findByAdminSubmissions(Pageable pageable);
    
}
