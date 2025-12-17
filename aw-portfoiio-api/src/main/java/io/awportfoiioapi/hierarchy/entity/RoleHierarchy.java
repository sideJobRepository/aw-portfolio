package io.awportfoiioapi.hierarchy.entity;

import io.awportfoiioapi.mapperd.DateSuperClass;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Table(name = "ROLE_HIERARCHY")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoleHierarchy extends DateSuperClass {

    @Id
    @Column(name = "ROLE_HIERARCHY_ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    
    @Column(name = "ROLE_NAME")
    private String roleName;
    
    // 단방향 자기참조 매핑: 상위 권한
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ROLE_ID")
    private RoleHierarchy parent;
    
}
