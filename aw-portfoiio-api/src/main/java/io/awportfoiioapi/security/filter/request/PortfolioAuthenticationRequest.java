package io.awportfoiioapi.security.filter.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PortfolioAuthenticationRequest {

    
    private String loginId;
    
    private String password;
}