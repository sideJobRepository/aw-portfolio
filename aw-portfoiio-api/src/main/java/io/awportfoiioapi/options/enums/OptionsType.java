package io.awportfoiioapi.options.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OptionsType {
    AGREEMENT_CHECK_BOX("동의체크박스"),
    SHORT_ANSWER("단답형"),
    LONG_ANSWER("장문형"),
    FILE("파일 업로드"),
    CHECK_BOX("체크박스(조건부 입력)");
    
    private final String value;
}
