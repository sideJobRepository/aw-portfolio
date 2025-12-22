package io.awportfoiioapi.options.entity;


import io.awportfoiioapi.mapperd.DateSuperClass;
import io.awportfoiioapi.options.enums.OptionsType;
import io.awportfoiioapi.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Table(name = "OPTIONS")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Options extends DateSuperClass {
    
    // 옵션 ID
    @Id
    @Column(name = "OPTIONS_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    // 질문 ID
    @JoinColumn(name = "QUESTION_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
    
    
    // 옵션 순서
    @Column(name = "OPTIONS_ORDERS")
    private Integer orders;
    
    // 옵션 제목
    @Column(name = "OPTIONS_TITLE")
    private String title;
    
    // 옵션 설명
    @Column(name = "OPTIONS_DESCRIPTION")
    private String description;
    
    // 옵션 타입
    @Column(name = "OPTIONS_TYPE")
    @Enumerated(EnumType.STRING)
    private OptionsType type;
    
    // 옵션 썸네일
    @Column(name = "OPTIONS_THUMBNAIL")
    private String thumbnail;
    
    // 옵션 최소 길이
    @Column(name = "OPTIONS_MIN_LENGTH")
    private Integer minLength;
    
    // 옵션 최대 길이
    @Column(name = "OPTIONS_MAX_LENGTH")
    private Integer maxLength;
    
    // 옵션 최소 길이 여부 활성
    @Column(name = "OPTIONS_MIN_LENGTH_IS_ACTIVE")
    private Boolean minLengthIsActive;
    // 옵션 여부 활성
    @Column(name = "OPTIONS_IS_ACTIVE")
    private Boolean optionsIsActive;
    
    public void updateBasic(
            Integer orders,
            String title,
            String description,
            OptionsType type,
            Integer minLength,
            Integer maxLength,
            Boolean minLengthIsActive,
            Boolean isRequired
    ) {
        this.orders = orders;
        this.title = title;
        this.description = description;
        this.type = type;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minLengthIsActive = minLengthIsActive;
        this.optionsIsActive = isRequired;
    }
    
    public void changeThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public void changeQuestion(Question question) {
        this.question = question;
    }
    
}
