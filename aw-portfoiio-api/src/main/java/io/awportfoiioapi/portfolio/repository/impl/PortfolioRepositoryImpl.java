package io.awportfoiioapi.portfolio.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.portfolio.repository.query.PortfolioQueryRepository;
import lombok.RequiredArgsConstructor;

import static io.awportfoiioapi.portfolio.entity.QPortfolio.portfolio;

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
}
