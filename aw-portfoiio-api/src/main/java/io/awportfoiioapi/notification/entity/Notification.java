package io.awportfoiioapi.notification.entity;

import io.awportfoiioapi.mapperd.DateSuperClass;
import io.awportfoiioapi.options.entity.Options;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Table(name = "NOTIFICATION")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notification extends DateSuperClass {
    
    
    // 안내사항 ID
    @Id
    @Column(name = "NOTIFICATION_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    // 옵션 ID
    @JoinColumn(name = "OPTIONS_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Options options;
    
    // 안내사항 설명
    @Column(name = "NOTIFICATION_DESCRIPTION")
    private String description;
    
    public void changeDescription(String value) {
        this.description = value;
    }
    
}
