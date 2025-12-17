package io.awportfoiioapi.refresh.repository.query;

import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.refresh.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenQueryRepository {
    
    Optional<RefreshToken> findPortfolioMember(Member member);
    
    Optional<RefreshToken> findPortfolioRefreshTokenValue(String refreshTokenValue);
}
