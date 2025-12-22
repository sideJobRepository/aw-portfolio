package io.awportfoiioapi.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionPostRequest {
    @NotNull(message = "포트폴리오 아이디는 필수입니다.")
    private Long portfolioId;
    @NotNull(message = "질문 단계는 필수입니다.")
    private Integer step;
    @NotNull(message = "질문 순서는 필수입니다.")
    private Integer order;
    @NotBlank(message = "질문 제목은 필수입니다.")
    private String title;
    @NotBlank(message = "질문 살명은 필수입니다.")
    private String description;
    @NotBlank(message = "질문 타입은 필수입니다.")
    private String type;
    private MultipartFile thumbnail;
    private Integer minLength;
    private Integer maxLength;
    private Boolean requireMinLength;
    private Boolean isRequired;
    private List<Notifications> notifications;
    
    public List<Notifications> getNotifications() {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>();
        }
        return this.notifications;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Notifications {
        private String value;
    }
}
