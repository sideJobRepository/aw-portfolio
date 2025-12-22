package io.awportfoiioapi.security.provider;


import io.awportfoiioapi.security.context.MemberContext;
import io.awportfoiioapi.security.service.impl.MemberDetailServiceImpl;
import io.awportfoiioapi.security.token.PortfolioAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component(value = "portfolioAuthenticationProvider")
@RequiredArgsConstructor
public class PortfolioAuthenticationProvider implements AuthenticationProvider {
    

    private final MemberDetailServiceImpl memberDetailService;
    
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        // 어드민
        // 1. 아이디존재하지는 확인 (없으면 exception)
        // 2. 비밀번호 맞는지 확인 (없으면 exception)
        //
        PortfolioAuthenticationToken loginAuthentication = (PortfolioAuthenticationToken) authentication;
      
        String loginId = (String) loginAuthentication.getPrincipal();
        String password = (String) loginAuthentication.getCredentials();
        String url = loginAuthentication.getUrl();
        String ip = loginAuthentication.getIp();
        
        
        if ("/api/admin-login".equals(url)) {
            MemberContext adminContext = (MemberContext) memberDetailService.loadUserByAdmin(loginId,password,ip);
            return new PortfolioAuthenticationToken(
                    adminContext.getMember(),
                    null,
                    adminContext.getAuthorities()
            );
            
        }
        MemberContext memberContext = (MemberContext) memberDetailService.loadUserByUsername(loginId,password,ip);
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
