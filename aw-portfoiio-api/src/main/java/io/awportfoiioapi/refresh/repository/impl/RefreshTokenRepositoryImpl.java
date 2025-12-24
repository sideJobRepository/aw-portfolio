package io.awportfoiioapi.refresh.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.refresh.entity.RefreshToken;
import io.awportfoiioapi.refresh.repository.query.RefreshTokenQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.awportfoiioapi.refresh.entity.QRefreshToken.refreshToken;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    
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
    
    @Override
    public void deleteByMember(Long id) {
        em.flush();
        queryFactory
                .delete(refreshToken)
                .where(refreshToken.member.id.eq(id))
                .execute();
    }
}
