package io.awportfoiioapi.security.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.advice.response.ErrorMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component("portfolioAuthenticationFailureHandler")
@RequiredArgsConstructor
public class PortfolioAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    private final ObjectMapper mapper;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String message = exception.getMessage();
        ErrorMessageResponse errorResponse = new ErrorMessageResponse("401", message);
        errorResponse.addValidation("message",message);
        mapper.writeValue(response.getWriter(), errorResponse);
    }
}
