package io.awportfoiioapi.resources.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "URL_RESOURCES")
@Getter
public class UrlResources {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "URL_RESOURCES_ID")
    private Long id;
    
    @Column(name = "URL_RESOURCES_PATH")
    private String urlResourcesPath;
    
    @Column(name = "URL_HTTP_METHOD")
    private String urlHttpMethod;
}
