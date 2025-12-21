package io.awportfoiioapi.portfolio.entity;

import io.awportfoiioapi.category.entity.Category;
import io.awportfoiioapi.mapperd.DateSuperClass;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPutRequest;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "PORTFOLIO")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Portfolio extends DateSuperClass {
    
    // 포트폴리오 ID
    @Id
    @Column(name = "PORTFOLIO_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    // 카테고리 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    
    // 포트폴리오 제목
    @Column(name = "PORTFOLIO_TITLE")
    private String title;
    
    // 포트폴리오 설명
    @Column(name = "PORTFOLIO_DESCRIPTION")
    private String description;
    
    // 포트폴리오 도메인
    @Column(name = "PORTFOLIO_DOMAIN")
    private String domain;
    
    // 포트폴리오 순서
    @Column(name = "PORTFOLIO_ORDERS")
    private Integer orders;
    
    // 포트폴리오 슬러그
    @Column(name = "PORTFOLIO_SLUG")
    private String slug;
    
    // 포트폴리오 썸네일
    @Column(name = "PORTFOLIO_THUMBNAIL")
    private String thumbnail;
    
    // 포트폴리오 여부 활성
    @Column(name = "PORTFOLIO_IS_ACTIVE")
    private Boolean isActive;
    
    public void update(PortfolioPutRequest request) {
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.slug = request.getSlug();
        this.domain = request.getDomain();
        this.orders = request.getOrder();
        this.isActive = request.getIsActive();
    }
    
    public void updateCategory(Category category) {
        this.category = category;
    }
    
    public void updateThumbnail(String url) {
        this.thumbnail = url;
    }
}
