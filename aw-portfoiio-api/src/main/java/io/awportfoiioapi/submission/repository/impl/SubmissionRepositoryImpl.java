package io.awportfoiioapi.submission.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.submission.dto.response.QSubmissionGetRequest;
import io.awportfoiioapi.submission.dto.response.SubmissionGetRequest;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.query.SubmissionQueryRepository;
import io.awportfoiioapi.submissions.dto.response.QSubmissionsGetRequest;
import io.awportfoiioapi.submissions.dto.response.QSubmissionsGetRequest_Portfolio;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static io.awportfoiioapi.portfolio.entity.QPortfolio.portfolio;
import static io.awportfoiioapi.submission.entity.QSubmission.submission;

@RequiredArgsConstructor
public class SubmissionRepositoryImpl implements SubmissionQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    private final EntityManager em;
    
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
                .join(submission.portfolio, portfolio).fetchJoin()
                .where(submission.member.id.eq(memberId))
                .fetch();
    }
    
    @Override
    public List<Long> findBySubmissionIds(Long id) {
        return queryFactory
                .select(submission.id)
                .from(submission)
                .where(submission.member.id.eq(id))
                .fetch();
    }
    
    @Override
    public void deleteByMemberSubmissions(Long id) {
        em.flush();
        queryFactory
                .delete(submission)
                .where(submission.member.id.eq(id))
                .execute();
    }
    
    public void deleteBySubmissions(Long id) {
            em.flush();
            queryFactory
                    .delete(submission)
                    .where(submission.id.eq(id))
                    .execute();
        }
    
    @Override
    public Page<SubmissionsGetRequest> findByAdminSubmissions(Pageable pageable) {
        List<SubmissionsGetRequest> result = queryFactory
                .select(
                        new QSubmissionsGetRequest(
                                submission.id,
                                submission.portfolio.id,
                                submission.companyName,
                                submission.submissionJson,
                                submission.isDraft,
                                submission.completedDate,
                                submission.modifyDate,
                                new QSubmissionsGetRequest_Portfolio(
                                        portfolio.title,
                                        portfolio.slug
                                )
                        )
                )
                .from(submission)
                .join(submission.portfolio, portfolio)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory.select(submission.count())
                .from(submission);
        
        return  PageableExecutionUtils.getPage(result,pageable,countQuery::fetchOne);
    }
}
