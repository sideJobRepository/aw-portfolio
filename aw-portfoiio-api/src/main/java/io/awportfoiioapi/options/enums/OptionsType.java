package io.awportfoiioapi.options.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum OptionsType {
    AGREEMENT("동의체크박스"),
    TEXT("단답형"),
    TEXTAREA("장문형"),
    FILE("파일 업로드"),
    CHECKBOX("체크박스(조건부 입력)"),
    PARLOR("객실"),
    SPECIAL("스페셜"),
    REFUND("환불"),
    MULTI_TEXT("멀티 텍스트"),
    CHECKBOX_INPUT("체크박스 인풋"),
    ADDR("주소");
    
    private final String value;
}
