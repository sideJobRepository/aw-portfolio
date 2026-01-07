package io.awportfoiioapi.question.respotiroy.impl;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.excel.dto.response.ExcelColumnResponse;
import io.awportfoiioapi.excel.dto.response.QExcelColumnResponse;
import io.awportfoiioapi.options.enums.OptionsType;
import io.awportfoiioapi.question.dto.response.QQuestionGetResponse;
import io.awportfoiioapi.question.dto.response.QuestionGetResponse;
import io.awportfoiioapi.question.entity.Question;
import io.awportfoiioapi.question.respotiroy.query.QuestionQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.awportfoiioapi.options.entity.QOptions.options;
import static io.awportfoiioapi.question.entity.QQuestion.question;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    
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
                .where(question.portfolio.id.eq(portfolioId), question.step.eq(step))
                .fetchFirst();
    }
    
    @Override
    public List<QuestionGetResponse> findByQuestions(Long portfolioId) {
        
        Map<Long, QuestionGetResponse> result =
                queryFactory
                        .from(options)
                        .join(options.question, question)
                        .where(question.portfolio.id.eq(portfolioId))
                        .transform(
                                GroupBy.groupBy(options.id).as(
                                        new QQuestionGetResponse(
                                                options.id,
                                                question.id,
                                                question.step,
                                                options.orders,
                                                options.title,
                                                options.description,
                                                options.type.stringValue().toLowerCase(),
                                                options.thumbnail,
                                                options.maxLength,
                                                options.minLength,
                                                options.minLengthIsActive,
                                                options.optionsIsActive,
                                                options.option
                                        )
                                )
                        );
        
        return new ArrayList<>(result.values());
    }
    
    @Override
    public List<ExcelColumnResponse> findByColumn(Long portfolioId) {
        return queryFactory
                .select(
                        new QExcelColumnResponse(
                                question.id,
                                options.id,
                                options.orders,
                                options.type.stringValue(),
                                options.title
                        )
                )
                .from(options)
                .join(options.question, question)
                .where(question.portfolio.id.eq(portfolioId) , options.type.ne(OptionsType.FILE))
                .orderBy(question.step.asc(),options.orders.asc())
                .fetch();

    }
    
    @Override
    public List<Question> findByPortfolioId(Long portfolioId) {
        return queryFactory
                .selectFrom(question)
                .where(question.portfolio.id.eq(portfolioId))
                .fetch();
    }
}
