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
    private List<String> roles;
    
    public static MemberResponseDto create(Member member, List<GrantedAuthority> authorities) {
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setId(member.getId());
        memberResponseDto.setName(member.getLoginId());
        List<String> roleList = new ArrayList<>();
        if (authorities != null && !authorities.isEmpty()) {
            for (GrantedAuthority auth : authorities) {
                roleList.add("ROLE_" + auth.getAuthority());
            }
        }
        memberResponseDto.setRoles(roleList);
        return memberResponseDto;
    }
}
