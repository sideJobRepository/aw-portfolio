package io.awportfoiioapi.security;

import io.awportfoiioapi.security.filter.PortfolioAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class PortfolioSecurityDsl<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, PortfolioSecurityDsl<H>, PortfolioAuthenticationFilter> {
    
    
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    
    private static final RequestMatcher LOGIN_MATCHER = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/user-login"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/admin-login")
    );
    
    
    public PortfolioSecurityDsl() {
        super(new PortfolioAuthenticationFilter(),null);
    }
    

    
    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }
    
    
    @Override
    public void configure(H http) throws Exception {
        
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        getAuthenticationFilter().setAuthenticationManager(authenticationManager);
        getAuthenticationFilter().setAuthenticationSuccessHandler(successHandler);
        getAuthenticationFilter().setAuthenticationFailureHandler(failureHandler);
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if(sessionAuthenticationStrategy != null) {
            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        http.setSharedObject(PortfolioAuthenticationFilter.class,getAuthenticationFilter());
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    
    public PortfolioSecurityDsl<H> portfolioSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }
    
    public PortfolioSecurityDsl<H> portfolioFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }
    
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return LOGIN_MATCHER;
    }
    
}
