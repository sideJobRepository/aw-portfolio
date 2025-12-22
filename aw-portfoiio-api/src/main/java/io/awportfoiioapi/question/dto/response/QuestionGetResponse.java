package io.awportfoiioapi.question.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class QuestionGetResponse {
    private Long id;
    private Long questionId;
    private Integer step;
    private Integer order;
    private String title;
    private String description;
    private String questionType;
    private String thumbnail;
    private Integer maxLength;
    private Integer minLength;
    private Boolean requireMinLength;
    private Boolean isRequired;
    private List<Notifications> notifications;
    
    public List<Notifications> getNotifications() {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>();
        }
        return this.notifications;
    }
    
    @QueryProjection
    public QuestionGetResponse(Long id, Long questionId, Integer step, Integer order, String title, String description, String type, String thumbnail, Integer maxLength, Integer minLength, Boolean minLengthIsActive, Boolean optionsIsActive) {
        this.id = id;
        this.questionId = questionId;
        this.step = step;
        this.order = order;
        this.title = title;
        this.description = description;
        this.questionType = type;
        this.thumbnail = thumbnail;
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.requireMinLength = minLengthIsActive;
        this.isRequired = optionsIsActive;
    }
    
     @Data
     @NoArgsConstructor
     public static class Notifications {
         private Long id;
         private String value;
         
         
        @QueryProjection
        public Notifications(Long id, String value) {
            this.id = id;
            this.value = value;
        }
     }
}
