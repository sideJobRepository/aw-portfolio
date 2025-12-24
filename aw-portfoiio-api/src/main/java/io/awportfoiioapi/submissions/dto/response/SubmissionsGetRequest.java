package io.awportfoiioapi.submissions.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class SubmissionsGetRequest {
    
    
    private Long id;
    private Long portfolioId;
    private String companyName;
    private String responses;
    private Boolean isDraft;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private Portfolio portfolio;
    
    @QueryProjection
    public SubmissionsGetRequest(Long id, Long portfolioId, String companyName, String responses, Boolean isDraft, LocalDateTime completedAt, LocalDateTime updatedAt,Portfolio portfolio) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.companyName = companyName;
        this.responses = responses;
        this.isDraft = isDraft;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
        this.portfolio = portfolio;
    }
    
    public Portfolio getPortfolio() {
        if(this.portfolio == null) {
            this.portfolio = new Portfolio();
        }
        return this.portfolio;
    }
    
    @Data
    @NoArgsConstructor
    public static class Portfolio {
        private String title;
        private String slug;
        
        @QueryProjection
        public Portfolio(String title, String slug) {
            this.title = title;
            this.slug = slug;
        }
    }
}
