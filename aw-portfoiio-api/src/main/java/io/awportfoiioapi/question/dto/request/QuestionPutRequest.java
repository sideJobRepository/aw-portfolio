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
public class QuestionPutRequest {
    @NotNull(message = "옵션 ID는 필수입니다.")
    private Long optionsId;
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
    private Integer minLength;
    private Integer maxLength;
    private Boolean requireMinLength;
    private Boolean isRequired;
    private ThumbnailRequest thumbnail;
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
        private Long id;
        private String value;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThumbnailRequest {
        private MultipartFile file;
        private Boolean remove;
    }
}
