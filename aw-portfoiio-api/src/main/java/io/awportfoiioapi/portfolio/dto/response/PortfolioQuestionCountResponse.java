package io.awportfoiioapi.portfolio.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PortfolioQuestionCountResponse {
    
    private Long portfolioId;
    private Long count;
    @QueryProjection
    public PortfolioQuestionCountResponse(Long portfolioId, Long count) {
        this.portfolioId = portfolioId;
        this.count = count;
    }
}
