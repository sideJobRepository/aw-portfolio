package io.awportfoiioapi.file.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonFileType {
    PORTFOLIO("포트폴리오"),
    OPTIONS("옵션"),
    SUBMISSION_OPTION("제출 옵션 파일");
    
    private final String value;
}
