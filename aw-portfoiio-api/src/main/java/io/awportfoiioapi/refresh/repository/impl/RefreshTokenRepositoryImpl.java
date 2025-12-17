package io.awportfoiioapi.refresh.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.refresh.entity.RefreshToken;
import io.awportfoiioapi.refresh.repository.query.RefreshTokenQueryRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.awportfoiioapi.refresh.entity.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<RefreshToken> findPortfolioMember(Member member) {
        RefreshToken token = queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.member.eq(member))
                .fetchOne();
        return Optional.ofNullable(token);
    }
    
    @Override
    public Optional<RefreshToken> findPortfolioRefreshTokenValue(String refreshTokenValue) {
        RefreshToken tone = queryFactory
                .selectFrom(refreshToken)
                .where(refreshToken.refreshTokenValue.eq(refreshTokenValue))
                .fetchOne();
        return Optional.ofNullable(tone);
    }
}
