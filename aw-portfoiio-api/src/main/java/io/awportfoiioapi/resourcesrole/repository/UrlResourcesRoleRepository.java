package io.awportfoiioapi.resourcesrole.repository;

import io.awportfoiioapi.resourcesrole.entity.UrlResourcesRole;
import io.awportfoiioapi.resourcesrole.repository.query.UrlResourcesRoleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlResourcesRoleRepository extends JpaRepository<UrlResourcesRole, Long> , UrlResourcesRoleQueryRepository {
    

}
