package io.awportfoiioapi.submission.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.portfolio.entity.QPortfolio;
import io.awportfoiioapi.submission.dto.response.QSubmissionGetRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.query.SubmissionQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.awportfoiioapi.portfolio.entity.QPortfolio.*;
import static io.awportfoiioapi.submission.entity.QSubmission.submission;

@RequiredArgsConstructor
public class SubmissionRepositoryImpl implements SubmissionQueryRepository {
    private final JPAQueryFactory queryFactory;
    
    @Override
    public SubmissionGetRequest getSubmission(Long submissionId) {
        return queryFactory
                .select(
                    new QSubmissionGetRequest(
                            submission.id,
                            submission.portfolio.id,
                            submission.companyName,
                            submission.isDraft,
                            submission.submissionJson,
                            submission.completedDate
                    )
                )
                .from(submission)
                .where(submission.id.eq(submissionId))
                .fetchFirst();
        
    }
    
    @Override
    public List<Submission> findBySubmissions(Long memberId) {
        return queryFactory
                .select(submission)
                .from(submission)
                .join(submission.portfolio ,portfolio).fetchJoin()
                .where(submission.member.id.eq(memberId))
                .fetch();
    }
}
