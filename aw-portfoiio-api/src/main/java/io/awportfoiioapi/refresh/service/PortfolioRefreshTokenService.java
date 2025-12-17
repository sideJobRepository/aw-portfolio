package io.awportfoiioapi.refresh.service;


import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.security.token.TokenAndUser;

import java.time.LocalDateTime;

public interface PortfolioRefreshTokenService {
    
    void refreshTokenSaveOrUpdate(Member member, String refreshTokenValue, LocalDateTime expiresAt);
    Member validateRefreshToken(String refreshToken);
    TokenAndUser reissueTokenWithUser(String refreshToken); // ← 이름/시그니처 통일
    ApiResponse deleteRefresh(String refreshToken);
}
