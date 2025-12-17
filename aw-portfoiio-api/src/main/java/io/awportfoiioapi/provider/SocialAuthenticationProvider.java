package io.awportfoiioapi.provider;


import io.awportfoiioapi.security.token.PortfolioAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component(value = "socialAuthenticationProvider")
@RequiredArgsConstructor
public class SocialAuthenticationProvider implements AuthenticationProvider {
    

   // private final BgmAgitMemberDetailService bgmAgitMemberDetailService;
    
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        PortfolioAuthenticationToken token = (PortfolioAuthenticationToken) authentication;
      
//          String socialType = token.getSocialLoginUrl().name();
//          String authorizeCode = (String) token.getPrincipal();
//
//          SocialService socialService = getSocialService(socialType);
//          AccessTokenResponse accessToken = socialService.getAccessToken(authorizeCode);
//          SocialProfile profile = socialService.getProfile(accessToken.getAccessToken());
//
//          BgmAgitMemberContext memberContext =
//                  (BgmAgitMemberContext) bgmAgitMemberDetailService.loadUserByUsername(profile);
//
//          return new SocialAuthenticationToken(
//                  memberContext.getBgmAgitMember(),
//                  null,
//                  null,
//                  memberContext.getAuthorities()
//          );
        return null;
        
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return PortfolioAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
}
