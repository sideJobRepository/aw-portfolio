package io.awportfoiioapi.options.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum OptionsType {
    AGREEMENT_CHECK_BOX("동의체크박스"),
    SHORT_ANSWER("단답형"),
    LONG_ANSWER("장문형"),
    FILE("파일 업로드"),
    CHECK_BOX("체크박스(조건부 입력)");
    
    private final String value;
    public static OptionsType fromValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("잘못된 옵션 타입입니다: " + value)
                );
    }
}
