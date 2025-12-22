package io.awportfoiioapi.question.respotiroy.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.question.entity.QQuestion;
import io.awportfoiioapi.question.entity.Question;
import io.awportfoiioapi.question.respotiroy.query.QuestionQueryRepository;
import lombok.RequiredArgsConstructor;

import static io.awportfoiioapi.options.entity.QOptions.options;
import static io.awportfoiioapi.question.entity.QQuestion.question;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Boolean existsStep(Long portfolioId,Integer step) {
        return queryFactory
                .selectOne()
                .from(question)
                .where(question.portfolio.id.eq(portfolioId) , question.step.eq(step))
                .fetchFirst() != null;
    }
    
    @Override
    public Boolean existsOrders(Long portfolioId, Integer step, Integer order) {
        return queryFactory
                .selectOne()
                .from(options)
                .join(options.question, question)
                .where(
                    question.portfolio.id.eq(portfolioId),
                    question.step.eq(step),
                    options.orders.eq(order)
                )
                .fetchFirst() != null;
    }
    
    @Override
    public Question findByPortfolioStep(Long portfolioId, Integer step) {
        return queryFactory
                .selectFrom(question)
                .where(question.portfolio.id.eq(portfolioId) , question.step.eq(step))
                .fetchFirst();
    }
}
