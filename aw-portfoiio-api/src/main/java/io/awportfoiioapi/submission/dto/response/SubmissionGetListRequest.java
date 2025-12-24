package io.awportfoiioapi.submission.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubmissionGetListRequest {

    private Long id;
    private Long portfolioId;
    private String companyName;
    private String responses;
    private Boolean isDraft;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private Portfolio portfolio;
    
    public SubmissionGetListRequest(Long id, Long portfolioId, String companyName, String responses, Boolean isDraft, LocalDateTime completedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.companyName = companyName;
        this.responses = responses;
        this.isDraft = isDraft;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
    }
    
    public Portfolio getPortfolio() {
        if(this.portfolio == null) {
            this.portfolio = new Portfolio();
        }
        return this.portfolio;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Portfolio {
        private String title;
        private String slug;
    }
}
