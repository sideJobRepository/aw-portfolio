package io.awportfoiioapi.question.entity;


import io.awportfoiioapi.mapperd.DateSuperClass;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Table(name = "QUESTION")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Question extends DateSuperClass {
    
    
    // 질문 ID
    @Id
    @Column(name = "QUESTION_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    // 포트폴리오 ID
    @JoinColumn(name = "PORTFOLIO_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    
    // 질문 단계
    @Column(name = "QUESTION_STEP")
    private Integer step;
}
