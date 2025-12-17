package io.awportfoiioapi.security.token;


import io.awportfoiioapi.security.dto.MemberResponseDto;

// 토큰 + 유저 응답용
public record TokenAndUser(TokenPair token, MemberResponseDto user) {}

