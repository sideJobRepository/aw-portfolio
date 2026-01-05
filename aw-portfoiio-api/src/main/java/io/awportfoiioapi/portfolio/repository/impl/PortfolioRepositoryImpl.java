package io.awportfoiioapi.portfolio.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.portfolio.dto.response.*;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import io.awportfoiioapi.portfolio.repository.query.PortfolioQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.awportfoiioapi.category.entity.QCategory.category;
import static io.awportfoiioapi.options.entity.QOptions.options;
import static io.awportfoiioapi.portfolio.entity.QPortfolio.portfolio;
import static io.awportfoiioapi.question.entity.QQuestion.question;
import static io.awportfoiioapi.submission.entity.QSubmission.submission;

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
    public Page<PortfolioResponse> getPortfolioList(Pageable pageable, String name) {
        List<PortfolioResponse> result = queryFactory
                .select(
                        new QPortfolioResponse(
                                portfolio.id,
                                portfolio.category.id,
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
                .where(whereName(name))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(portfolio.orders.asc())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(portfolio.count())
                .from(portfolio)
                .where(whereName(name));
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
    
    @Override
    public List<PortfolioResponse> getPortfolioList() {
        return queryFactory
                .select(
                        new QPortfolioResponse(
                                portfolio.id,
                                portfolio.category.id,
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
                .orderBy(portfolio.orders.asc())
                .fetch();
    }
    
    @Override
    public List<PortfolioResponse> getPortfolioList(Boolean active, Long categoryId) {
        return queryFactory
                .select(
                        new QPortfolioResponse(
                                portfolio.id,
                                portfolio.category.id,
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
                .where(whereIsAction(active), whereCategoryId(categoryId))
                .orderBy(portfolio.category.categoryOrders.asc() ,  portfolio.orders.asc())
                .fetch();
    }
    
    @Override
    public List<PortfoliosGetDetailResponse> getPortfolioDetailOptions(Long id) {
        return queryFactory
                .select(
                        new QPortfoliosGetDetailResponse(
                                options.id,
                                portfolio.id,
                                options.description,
                                options.optionsIsActive,
                                options.maxLength,
                                options.minLength,
                                options.option,
                                options.orders,
                                options.type.stringValue().toLowerCase(),
                                options.minLengthIsActive,
                                question.step,
                                options.thumbnail,
                                options.title
                        )
                )
                .from(options)
                .join(options.question, question)
                .join(question.portfolio, portfolio)
                .where(portfolio.id.eq(id))
                .orderBy(question.step.asc(), options.orders.asc())
                .fetch();
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
    
    @Override
    public List<PortfolioSubmissionCountResponse> findBySubmissionCount() {
        return queryFactory
                .select(new QPortfolioSubmissionCountResponse(
                        portfolio.id,
                        submission.count()
                ))
                .from(submission)
                .leftJoin(submission.portfolio, portfolio)
                .where(submission.isDraft.eq(false))
                .groupBy(portfolio.id)
                .fetch();
    }
    
    @Override
    public PortfolioGetDetailResponse findByPortfolioDetail(Long id) {
        return queryFactory
                .select(new QPortfolioGetDetailResponse(
                        portfolio.id,
                        portfolio.category.id,
                        portfolio.title,
                        portfolio.description,
                        portfolio.domain,
                        portfolio.orders,
                        portfolio.slug,
                        portfolio.thumbnail,
                        portfolio.isActive
                ))
                .from(portfolio)
                .where(portfolio.id.eq(id))
                .fetchFirst();
    }
    
    @Override
    public Portfolio getPortfolio(Long id) {
        return queryFactory
                .select(portfolio)
                .from(portfolio)
                .join(portfolio.category, category).fetchJoin()
                .where(portfolio.id.eq(id))
                .fetchFirst();
    }
    
    private BooleanExpression whereIsAction(Boolean active) {
        if (active == null) {
            return null;
        }
        return portfolio.isActive.eq(active);
    }
    
    private BooleanExpression whereCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return portfolio.category.id.eq(categoryId);
    }
    
    private BooleanExpression whereName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return portfolio.title.like("%" + name + "%");
    }
}
