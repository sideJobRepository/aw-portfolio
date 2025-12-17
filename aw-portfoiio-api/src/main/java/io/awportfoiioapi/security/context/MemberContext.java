package io.awportfoiioapi.security.context;

import io.awportfoiioapi.member.entrity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberContext implements UserDetails {
    
    private final Member member;
    private final List<GrantedAuthority> authorities;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
    
    @Override
    public String getPassword() {
        return member.getPassword();
    }
    
    @Override
    public String getUsername() {
        return member.getLoginId();
    }
}
