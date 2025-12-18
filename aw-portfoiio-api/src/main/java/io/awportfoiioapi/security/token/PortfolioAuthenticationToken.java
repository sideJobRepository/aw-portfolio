package io.awportfoiioapi.security.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.net.URI;
import java.util.Collection;

@Getter
public class PortfolioAuthenticationToken extends AbstractAuthenticationToken {
    
    private final Object principal;
    
    private final Object credentials;
    
    private String url;
    
    public PortfolioAuthenticationToken(String principal,String credentials,String url) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.url = url;
        setAuthenticated(false);
    }
    
    public PortfolioAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return this.credentials;
    }
    
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
    
    
}

