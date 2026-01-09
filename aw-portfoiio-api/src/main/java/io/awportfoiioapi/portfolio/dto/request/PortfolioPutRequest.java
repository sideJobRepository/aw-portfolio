package io.awportfoiioapi.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PortfolioPutRequest {
    @NotNull(message = "포트폴리오 ID는 필수입니다.")
    private Long id;
    private Long categoryId;
    @NotBlank(message = "포트폴리오 제목은 필수입니다.")
    private String title;
    @NotBlank(message = "포트폴리오 설명은 필수입니다.")
    private String description;
    @NotBlank(message = "포트폴리오 도메인은 필수입니다.")
    private String domain;
    @NotNull(message = "포트폴리오 순서는 필수입니다.")
    private Integer order;
    private String slug;
    private ThumbnailRequest thumbnail;
    @NotNull(message = "포트폴리오 활성여부는 필수입니다.")
    private Boolean isActive;
    private String mood;
    
    @Data
    public static class ThumbnailRequest{
        private MultipartFile file;
        private Boolean remove;
    }
}
