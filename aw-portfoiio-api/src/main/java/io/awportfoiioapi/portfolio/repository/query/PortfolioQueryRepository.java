package io.awportfoiioapi.portfolio.repository.query;

import io.awportfoiioapi.portfolio.dto.response.PortfolioQuestionCountResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfolioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortfolioQueryRepository {
    
    boolean existsByPortfolioOrder(Integer order);
    
    boolean existsByPortfolioOrder(Integer order, Long excludeId);
    
    Page<PortfolioResponse> getPortfolioList(Pageable pageable);
    
    List<PortfolioQuestionCountResponse> findByQuestionCount();
}
