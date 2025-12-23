package io.awportfoiioapi.submission.entity;

import io.awportfoiioapi.mapperd.DateSuperClass;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.submission.dto.request.SubmissionPostDraftRequest;
import io.awportfoiioapi.submission.dto.request.SubmissionPostRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Table(name = "SUBMISSION")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Submission extends DateSuperClass {
    
    
    // 제출 ID
    @Id
    @Column(name = "SUBMISSION_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    // 포트폴리오 ID
    @JoinColumn(name = "PORTFOLIO_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    
    @JoinColumn(name = "MEMBER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    
    // 제출 회사 이름
    @Column(name = "SUBMISSION_COMPANY_NAME")
    private String companyName;
    
    // 제출 비밀번호
    @Column(name = "SUBMISSION_PASSWORD")
    private String password;
    
    // 제출 제이슨
    @Column(name = "SUBMISSION_JSON", columnDefinition = "TEXT")
    private String submissionJson;
    
    // 제출 여부 드래프트
    @Column(name = "SUBMISSION_IS_DRAFT")
    private Boolean isDraft;
    
    // 제출 완료 일시
    @Column(name = "SUBMISSION_COMPLETED_DATE")
    private LocalDateTime completedDate;
    
    public void modifyJson(SubmissionPostDraftRequest request) {
        this.submissionJson = request.getResponse();
    }
    
    public void modifySubmission(SubmissionPostRequest request) {
        this.submissionJson = request.getResponse();
        this.completedDate = LocalDateTime.now();
        this.isDraft = false;
    }
}
