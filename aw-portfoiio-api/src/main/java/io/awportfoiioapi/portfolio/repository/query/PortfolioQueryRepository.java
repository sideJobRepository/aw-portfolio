package io.awportfoiioapi.portfolio.repository.query;

public interface PortfolioQueryRepository {
    
    boolean existsByPortfolioOrder(Integer order);
    
    boolean existsByPortfolioOrder(Integer order, Long excludeId);
}
