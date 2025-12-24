package io.awportfoiioapi.submission.repository.query;

import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;

import java.util.List;

public interface SubmissionQueryRepository {
    SubmissionGetRequest getSubmission(Long submissionId);
    
    List<Submission> findBySubmissions(Long id);
    
}
