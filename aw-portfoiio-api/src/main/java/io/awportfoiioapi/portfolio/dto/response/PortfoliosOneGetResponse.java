package io.awportfoiioapi.portfolio.dto.response;

import io.awportfoiioapi.portfolio.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfoliosOneGetResponse {
    
    private Long id;
    private Long categoryId;
    private String title;
    private String description;
    private String slug;
    private String thumbnail;
    private String domain;
    private Boolean isActive;
    private Integer order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Category category;
    private Count count;
    
    public Count getCount() {
        if(this.count == null){
            this.count = new Count();
        }
        return this.count;
    }
    
    public PortfoliosOneGetResponse(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.title = portfolio.getTitle();
        this.description = portfolio.getDescription();
        this.slug = portfolio.getSlug();
        this.thumbnail = portfolio.getThumbnail();
        this.domain = portfolio.getDomain();
        this.isActive = portfolio.getIsActive();
        this.order = portfolio.getOrders();
        this.createdAt = portfolio.getRegistDate();
        this.updatedAt = portfolio.getModifyDate();
        if (portfolio.getCategory() != null) {
            this.categoryId = portfolio.getCategory().getId();
            this.category = new Category(
                    portfolio.getCategory().getId(),
                    portfolio.getCategory().getCategoryName(),
                    portfolio.getCategory().getCategorySlug(),
                    portfolio.getCategory().getCategoryOrders(),
                    portfolio.getCategory().getRegistDate(),
                    portfolio.getCategory().getModifyDate()
            );
        }
        
    }
    
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Category {
        private Long id;
        private String name;
        private String slug;
        private Integer order;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Count {
        private Long questions;
        private Long submissions;
    }
}
