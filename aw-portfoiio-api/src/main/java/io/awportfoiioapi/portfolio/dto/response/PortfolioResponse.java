package io.awportfoiioapi.portfolio.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PortfolioResponse {
    private Long id;
    private Long categoryId;
    private String title;
    private String description;
    private String domain;
    private Integer order;
    private String slug;
    private String thumbnail;
    private Boolean isActive;
    private String mood;
    private Count count;
    
    public Count getCount() {
        if (this.count == null) {
            this.count = new Count();
        }
        return this.count;
    }
    
    @QueryProjection
    public PortfolioResponse(Long id, Long categoryId, String title, String description, String domain, Integer order, String slug, String thumbnail, Boolean isActive, String mood) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.domain = domain;
        this.order = order;
        this.slug = slug;
        this.thumbnail = thumbnail;
        this.isActive = isActive;
        this.mood = mood;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Count {
        private Long questions;
        private Long submissions;
    }
}
