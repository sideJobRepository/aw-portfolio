package io.awportfoiioapi.login;

import io.awportfoiioapi.ControllerTestSupport;
import io.awportfoiioapi.security.filter.request.PortfolioAuthenticationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;


public class LoginTest extends ControllerTestSupport {
    
    
    @DisplayName("어드민 로그인 테스트")
    @Test
    void test1() throws Exception {
        
        
         //이메일: admin@example.com
        // 비밀번호: admin123
        PortfolioAuthenticationRequest request = new PortfolioAuthenticationRequest("admin@example.com", "admin123");
        
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/admin-login")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andDo(print());
    }
    
    @DisplayName("어드민 로그인 검증(아이디)")
    @Test
    void test2() throws Exception {
        PortfolioAuthenticationRequest request = new PortfolioAuthenticationRequest("admin@example.co", "admin123");
        
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/admin-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print());
    }
    
    @DisplayName("어드민 로그인 검증(비밀번호)")
    @Test
    void test3() throws Exception {
        PortfolioAuthenticationRequest request = new PortfolioAuthenticationRequest("admin@example.com", "admin12");
        
        String json = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/admin-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print());
    
    }
}
