package io.awportfoiioapi.member.entrity;

import io.awportfoiioapi.mapperd.DateSuperClass;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Table(name = "MEMBER")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends DateSuperClass {
    
    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @Column(name = "MEMBER_LOGIN_ID")
    private String loginId;
    
    @Column(name = "MEMBER_PASSWORD")
    private String password;
    
    @Column(name = "MEMBER_IP")
    private String ip;
    
    @Column(name = "MEMBER_NAME")
    private String name;
    
    public void modifyIp(String ip) {
        this.ip = ip;
    }
    
    public void modifyPassword(String encode) {
        this.password = encode;
    }
}
