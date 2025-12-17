package io.awportfoiioapi.resourcesrole.entity;

import io.awportfoiioapi.resources.entity.UrlResources;
import io.awportfoiioapi.role.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "URL_RESOURCES_ROLE")
@Getter
public class UrlResourcesRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "URL_RESOURCES_ROLE_ID")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "URL_RESOURCES_ID")
    private UrlResources urlResources;
}
