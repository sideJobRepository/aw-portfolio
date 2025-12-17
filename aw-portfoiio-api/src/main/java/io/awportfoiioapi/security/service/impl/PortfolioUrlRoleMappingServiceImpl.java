package io.awportfoiioapi.security.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.security.service.PortfolioUrlRoleMappingService;
import io.awportfoiioapi.security.service.response.QRoleMapResponse;
import io.awportfoiioapi.security.service.response.RoleMapResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.awportfoiioapi.resources.entity.QUrlResources.*;
import static io.awportfoiioapi.resourcesrole.entity.QUrlResourcesRole.*;
import static io.awportfoiioapi.role.entity.QRole.*;

@Service
@Transactional(readOnly = true)
public class PortfolioUrlRoleMappingServiceImpl implements PortfolioUrlRoleMappingService {
    private LinkedHashMap<String, String> urlRoleMappings = new LinkedHashMap<>();
      private final JPAQueryFactory queryFactory;
      
      public PortfolioUrlRoleMappingServiceImpl(JPAQueryFactory queryFactory) {
          this.queryFactory = queryFactory;
      }
      
      public Map<String,String> getRoleMappings() {
          
          urlRoleMappings.clear();
          List<RoleMapResponse> resourcesList = queryFactory
                  .select(new QRoleMapResponse(
                          urlResources.urlResourcesPath,
                          role.roleName,
                          urlResources.urlHttpMethod
                  ))
                  .from(urlResourcesRole)
                  .join(urlResourcesRole.urlResources, urlResources)
                  .join(urlResourcesRole.role, role)
                  .fetch();

          resourcesList
                  .forEach(resources -> {
                      String key = resources.getHttpMethod().toUpperCase() + " " + resources.getResourcesPath(); // "POST /bgm-agit/notice"
                      urlRoleMappings.put(key, "ROLE_" + resources.getRoleName());
                  });
          return urlRoleMappings;
      }
}
