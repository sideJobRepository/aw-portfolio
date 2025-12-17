package io.awportfoiioapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.security.filter.request.PortfolioAuthenticationRequest;
import io.awportfoiioapi.security.token.PortfolioAuthenticationToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class PortfolioAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    
    
    public PortfolioAuthenticationFilter() {
        super(new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults()
                        .matcher(HttpMethod.POST, "/api/login")
        ));
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        
        String uri = request.getRequestURI();
        
        ObjectMapper objectMapper = new ObjectMapper();
        PortfolioAuthenticationRequest loginRequest = objectMapper.readValue(request.getReader(), PortfolioAuthenticationRequest.class);
        
        if (StringUtils.hasText(loginRequest.getLoginId()) || StringUtils.hasText(loginRequest.getPassword())) {
            throw new AuthenticationServiceException("code 값이 없습니다.");
        }
        PortfolioAuthenticationToken authRequest = new PortfolioAuthenticationToken(
                loginRequest.getLoginId(),
                loginRequest.getPassword()
        );
        
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
