package io.awportfoiioapi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    
    private Long id;
    
    private String loginId;
    
    private String password;
    
    private List<String> roles;
}
