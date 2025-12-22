package io.awportfoiioapi.question.dto.request;

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
    private Long optionsId;
    private Integer step;
    private Integer order;
    private String title;
    private String description;
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
