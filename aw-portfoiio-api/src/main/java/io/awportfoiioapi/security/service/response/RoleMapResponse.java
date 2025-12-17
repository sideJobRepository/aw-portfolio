package io.awportfoiioapi.security.service.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleMapResponse {
    
    private String resourcesPath;
    private String roleName;
    private String httpMethod;
    
    @QueryProjection
    public RoleMapResponse(String resourcesPath, String roleName, String httpMethod) {
        this.resourcesPath = resourcesPath;
        this.roleName = roleName;
        this.httpMethod = httpMethod;
    }
}
