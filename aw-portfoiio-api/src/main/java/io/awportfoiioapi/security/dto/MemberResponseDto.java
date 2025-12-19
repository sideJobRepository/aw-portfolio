package io.awportfoiioapi.security.dto;

import io.awportfoiioapi.member.entrity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String role;
    
    public static MemberResponseDto create(Member member, List<GrantedAuthority> authorities) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setId(member.getId());
        memberResponseDto.setEmail(member.getLoginId());
      
        if (authorities != null && !authorities.isEmpty()) {
            for (GrantedAuthority auth : authorities) {
                memberResponseDto.setRole(auth.getAuthority());
                if("SUPER_ADMIN".equals(memberResponseDto.getRole())) {
                    memberResponseDto.setName("최고 관리자");
                }else if("ADMIN".equals(memberResponseDto.getRole())) {
                    memberResponseDto.setName("관리자");
                }else {
                    memberResponseDto.setName("회원");
                }
            }
        }
        return memberResponseDto;
    }
}
