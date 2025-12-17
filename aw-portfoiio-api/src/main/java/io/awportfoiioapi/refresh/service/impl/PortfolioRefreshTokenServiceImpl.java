package io.awportfoiioapi.refresh.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.memberrole.service.MemberRoleService;
import io.awportfoiioapi.refresh.entity.RefreshToken;
import io.awportfoiioapi.refresh.repository.RefreshTokenRepository;
import io.awportfoiioapi.refresh.service.PortfolioRefreshTokenService;
import io.awportfoiioapi.security.dto.MemberResponseDto;
import io.awportfoiioapi.security.jwt.RsaSecuritySigner;
import io.awportfoiioapi.security.token.TokenAndUser;
import io.awportfoiioapi.security.token.TokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioRefreshTokenServiceImpl implements PortfolioRefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRoleService memberRoleService;
    private final RsaSecuritySigner rsaSecuritySigner;
    private final JWK jwk;
    
    @Override
    public void refreshTokenSaveOrUpdate(Member member, String refreshTokenValue, LocalDateTime expiresAt) {
        RefreshToken token = refreshTokenRepository
                .findPortfolioMember(member)
                .orElse(RefreshToken.builder()
                        .member(member)
                        .refreshTokenValue(refreshTokenValue)
                        .refreshExpiresDate(expiresAt)
                        .build()
                );
        if (token.getId() != null) {
            token.updateToken(refreshTokenValue, expiresAt);
        }
        refreshTokenRepository.save(token);
    }
    
    
    @Override
    public TokenAndUser reissueTokenWithUser(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        
        Member member = validateRefreshToken(refreshToken);
        
        if(member == null) {
            return null;
        }
        
        String roleName = memberRoleService
                .getMemberRole(member.getId())
                .getRole()
                .getRoleName();
        
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));
        
        try {
            TokenPair token = rsaSecuritySigner.getToken(member, jwk, authorities);
            
            refreshTokenSaveOrUpdate(
                    member,
                    token.refreshToken(),
                    LocalDateTime.now().plusDays(1)
            );
            
            // 로그인 때와 동일하게 DTO 생성
            MemberResponseDto user = MemberResponseDto.create(member, authorities);
            
            return new TokenAndUser(token, user);
        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패", e);
        }
    }
    
    @Override
    public ApiResponse deleteRefresh(String request) {
        RefreshToken bgmAgitRefreshToken = refreshTokenRepository.findPortfolioRefreshTokenValue(request).orElseThrow(() -> new RuntimeException("존재하지않는 리프레쉬 토큰입니다."));
        refreshTokenRepository.delete(bgmAgitRefreshToken);
        return new ApiResponse(200,true,"정상 삭제");
    }
    
    @Override
    public Member validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository
                .findPortfolioRefreshTokenValue(refreshToken)
                .orElse(null);
        
        if (token == null) {
            return null;
        }
        
        if (token.getRefreshExpiresDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }
        
        return token.getMember(); // fetch join 필요시 수정
    }
}
