package io.awportfoiioapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.advice.exception.ValidationException;
import io.awportfoiioapi.advice.response.ErrorMessageResponse;
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
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/user-login"),
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/admin-login")
        ));
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        
        
        String url = request.getRequestURI();
        
        String clientIp = getClientIp(request);
        
      
        ObjectMapper objectMapper = new ObjectMapper();
        PortfolioAuthenticationRequest loginRequest = objectMapper.readValue(request.getReader(), PortfolioAuthenticationRequest.class);
        
        ErrorMessageResponse error = new ErrorMessageResponse("400", null);
        
        if (!StringUtils.hasText(loginRequest.getLoginId())) {
            error.addValidation("loginId", "아이디는 필수 입력입니다.");
        }
        
        if (!StringUtils.hasText(loginRequest.getPassword())) {
            error.addValidation("password", "비밀번호는 필수 입력입니다.");
        }
        
        if (error.getValidation() != null && !error.getValidation().isEmpty()) {
            throw new AuthenticationServiceException(
                    "400",
                    new ValidationException(error)
            );
        }
        PortfolioAuthenticationToken authRequest = new PortfolioAuthenticationToken(
                loginRequest.getLoginId(),
                loginRequest.getPassword(),
                url,
                clientIp
        );
        
        return this.getAuthenticationManager().authenticate(authRequest);
    }
    
    public static String getClientIp(HttpServletRequest request) {
    
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP"
        };
    
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For는 "client, proxy1, proxy2" 형태
                return ip.split(",")[0].trim();
            }
        }
    
        return request.getRemoteAddr();
    }
}
