package io.awportfoiioapi.security.handler;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import io.awportfoiioapi.member.entrity.Member;
import io.awportfoiioapi.refresh.service.PortfolioRefreshTokenService;
import io.awportfoiioapi.security.dto.MemberResponseDto;
import io.awportfoiioapi.security.jwt.RsaSecuritySigner;
import io.awportfoiioapi.security.token.PortfolioAuthenticationToken;
import io.awportfoiioapi.security.token.TokenPair;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component(value = "portfolioAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class PortfolioAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final ObjectMapper objectMapper;
    private final RsaSecuritySigner rsaSecuritySigner;
    private final PortfolioRefreshTokenService portfolioRefreshTokenService;
    private final JWK jwk;
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 1;
    @Value("${cookie.secure}")
    private boolean secure;

    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PortfolioAuthenticationToken token = (PortfolioAuthenticationToken) authentication;
        Member member = (Member) token.getPrincipal();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) token.getAuthorities();
        
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);
        
        try {
            TokenPair tokenPair = rsaSecuritySigner.getToken(member, jwk, authorities);
            portfolioRefreshTokenService.refreshTokenSaveOrUpdate(member, tokenPair.refreshToken(), expiresAt);
            
            MemberResponseDto memberResponseDto = MemberResponseDto.create(member, authorities);
            // Access Token은 응답 JSON에 포함
            Map<String, Object> result = Map.of(
                    "user", memberResponseDto,
                    "token", tokenPair.accessToken()
            );
            
            // Refresh Token은 HttpOnly 쿠키로 설정
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenPair.refreshToken())
                    .httpOnly(true)
                    .secure(secure) // 로컬일 경우 secure=false
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } catch (JOSEException e) {
            throw new RuntimeException("JWT 생성 실패", e);
        }
    }
}
