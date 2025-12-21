package io.awportfoiioapi.portfolio.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.portfolio.dto.response.*;
import io.awportfoiioapi.portfolio.repository.query.PortfolioQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static io.awportfoiioapi.options.entity.QOptions.options;
import static io.awportfoiioapi.portfolio.entity.QPortfolio.portfolio;
import static io.awportfoiioapi.question.entity.QQuestion.question;

@RequiredArgsConstructor
public class PortfolioRepositoryImpl implements PortfolioQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public boolean existsByPortfolioOrder(Integer order) {
        return queryFactory
                .selectFrom(portfolio)
                .where(portfolio.orders.eq(order))
                .fetchFirst() != null;
    }
    
    @Override
    public boolean existsByPortfolioOrder(Integer order, Long excludeId) {
        return queryFactory
                .selectFrom(portfolio)
                .where(
                        portfolio.orders.eq(order),
                        portfolio.id.ne(excludeId)
                )
                .fetchFirst() != null;
    }
    
    @Override
    public Page<PortfolioResponse> getPortfolioList(Pageable pageable) {
        List<PortfolioResponse> result = queryFactory
                .select(
                        new QPortfolioResponse(
                                portfolio.id,
                                portfolio.title,
                                portfolio.description,
                                portfolio.domain,
                                portfolio.orders,
                                portfolio.slug,
                                portfolio.thumbnail,
                                portfolio.isActive
                        )
                )
                .from(portfolio)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(portfolio.count())
                .from(portfolio);
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
    
    @Override
    public List<PortfolioQuestionCountResponse> findByQuestionCount() {
        return queryFactory
                .select(
                        new QPortfolioQuestionCountResponse(
                                portfolio.id,
                                options.count()
                        ))
                .from(portfolio)
                .leftJoin(question)
                .on(question.portfolio.id.eq(portfolio.id))
                .leftJoin(options)
                .on(options.question.id.eq(question.id))
                .groupBy(portfolio.id)
                .fetch();
    }
}
