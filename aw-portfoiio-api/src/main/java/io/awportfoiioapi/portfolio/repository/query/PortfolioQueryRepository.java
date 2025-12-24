package io.awportfoiioapi.portfolio.repository.query;

import io.awportfoiioapi.portfolio.dto.response.*;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortfolioQueryRepository {
    
    boolean existsByPortfolioOrder(Integer order);
    
    boolean existsByPortfolioOrder(Integer order, Long excludeId);
    
    Page<PortfolioResponse> getPortfolioList(Pageable pageable);
    List<PortfolioResponse> getPortfolioList(Boolean active,Long categoryId);
    
    List<PortfoliosGetDetailResponse>  getPortfolioDetailOptions(Long id);
    List<PortfolioQuestionCountResponse> findByQuestionCount();
    List<PortfolioSubmissionCountResponse> findBySubmissionCount();
    
    PortfolioGetDetailResponse findByPortfolioDetail(Long id);
    Portfolio getPortfolio(Long id);
    
    
}
