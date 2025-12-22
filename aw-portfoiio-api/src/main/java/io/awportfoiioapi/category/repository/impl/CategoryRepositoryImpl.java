package io.awportfoiioapi.category.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.category.dto.response.CategoryCountResponse;
import io.awportfoiioapi.category.dto.response.CategoryGetResponse;
import io.awportfoiioapi.category.dto.response.QCategoryCountResponse;
import io.awportfoiioapi.category.dto.response.QCategoryGetResponse;
import io.awportfoiioapi.category.entity.Category;
import io.awportfoiioapi.category.repository.query.CategoryQueryRepository;
import io.awportfoiioapi.portfolio.entity.QPortfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.print.attribute.standard.QueuedJobCount;
import java.util.List;

import static io.awportfoiioapi.category.entity.QCategory.category;
import static io.awportfoiioapi.portfolio.entity.QPortfolio.*;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Page<CategoryGetResponse> getCategoryList(Pageable pageable) {
        List<CategoryGetResponse> result = queryFactory
                .select(
                        new QCategoryGetResponse(
                                category.id,
                                category.categoryName,
                                category.categoryOrders,
                                category.categorySlug,
                                category.registDate,
                                category.modifyDate
                        )
                )
                .from(category)
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber())
                .orderBy(category.id.desc())
                .fetch();
        
        JPAQuery<Long> countQuery = queryFactory
                .select(category.count())
                .from(category);
        
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }
    
    @Override
    public List<CategoryCountResponse> getCategoryCount() {
        return queryFactory
                .select(
                        new QCategoryCountResponse(
                                category.id,
                                category.count()
                        )
                )
                .from(portfolio)
                .join(portfolio.category, category)
                .groupBy(category.id)
                .fetch();
    }
    
    @Override
    public boolean existsByOrder(Integer order) {
        return queryFactory
                .selectFrom(category)
                .where(category.categoryOrders.eq(order))
                .fetchFirst() != null;
    }
    
    @Override
    public boolean existsByOrder(Integer order, Long excludeCategoryId) {
        return queryFactory
                .selectFrom(category)
                .where(
                        category.categoryOrders.eq(order),
                        category.id.ne(excludeCategoryId)
                )
                .fetchFirst() != null;
    }
    
    @Override
    public boolean existsByPortfolio(Long id) {
        return queryFactory
                .select(portfolio)
                .from(portfolio)
                .join(portfolio.category, category)
                .where(category.id.eq(id))
                .fetchFirst() != null;
    }
}
