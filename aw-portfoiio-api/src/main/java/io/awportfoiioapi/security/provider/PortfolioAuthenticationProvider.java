package io.awportfoiioapi.security.provider;


import io.awportfoiioapi.security.context.MemberContext;
import io.awportfoiioapi.security.service.impl.MemberDetailServiceImpl;
import io.awportfoiioapi.security.token.PortfolioAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component(value = "portfolioAuthenticationProvider")
@RequiredArgsConstructor
public class PortfolioAuthenticationProvider implements AuthenticationProvider {
    

    private final MemberDetailServiceImpl memberDetailService;
    
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        PortfolioAuthenticationToken token = (PortfolioAuthenticationToken) authentication;
      
        String loginId = (String) token.getPrincipal();
        String password = (String) token.getCredentials();
        

          MemberContext memberContext = (MemberContext) memberDetailService.loadUserByUsername(loginId,password);

          return new PortfolioAuthenticationToken(
                  memberContext.getMember(),
                  null,
                  memberContext.getAuthorities()
          );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return PortfolioAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
}
