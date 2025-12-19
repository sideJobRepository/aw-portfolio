package io.awportfoiioapi.security.dto;

import io.awportfoiioapi.member.entrity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String roles;
    
    public static MemberResponseDto create(Member member, List<GrantedAuthority> authorities) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setId(member.getId());
        memberResponseDto.setEmail(member.getLoginId());
      
        if (authorities != null && !authorities.isEmpty()) {
            for (GrantedAuthority auth : authorities) {
                memberResponseDto.setRoles("ROLE_" + auth.getAuthority());
                if(memberResponseDto.getRoles().equals("SUPER_ADMIN")) {
                    memberResponseDto.setName("최고 관리자");
                }
            }
        }
        return memberResponseDto;
    }
}
