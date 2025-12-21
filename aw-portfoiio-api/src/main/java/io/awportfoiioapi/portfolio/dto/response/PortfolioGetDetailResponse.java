package io.awportfoiioapi.portfolio.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@Data
public class PortfolioGetDetailResponse {
    private Long id;
    private Long categoryId;
    private String title;
    private String description;
    private String domain;
    private Long order;
    private String slug;
    private String thumbnail;
    private Boolean isActive;
    @QueryProjection
    public PortfolioGetDetailResponse(Long id, Long categoryId, String title, String description, String domain, Long order, String slug, String thumbnail, Boolean isActive) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.domain = domain;
        this.order = order;
        this.slug = slug;
        this.thumbnail = thumbnail;
        this.isActive = isActive;
    }
}
