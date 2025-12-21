package io.awportfoiioapi.file.entity;


import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.mapperd.DateSuperClass;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "COMMON_FILE")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommonFile extends DateSuperClass {
    
    // 공통 파일 ID
    @Id
    @Column(name = "COMMON_FILE_ID")
    private Long id;
    
    // 공통 파일 타겟 ID
    @Column(name = "COMMON_FILE_TARGET_ID")
    private Long fileTargetId;
    
    // 공통 파일 이름
    @Column(name = "COMMON_FILE_NAME")
    private String fileName;
    
    // 공통 파일 UUID 이름
    @Column(name = "COMMON_FILE_UUID_NAME")
    private String fileUuidName;
    
    // 공통 파일 타입
    @Column(name = "COMMON_FILE_TYPE")
    @Enumerated(EnumType.STRING)
    private CommonFileType fileType;
    
    // 공통 파일 URL
    @Column(name = "COMMON_FILE_URL")
    private String fileUrl;
}
