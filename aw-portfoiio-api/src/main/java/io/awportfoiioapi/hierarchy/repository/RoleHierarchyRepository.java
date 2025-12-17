package io.awportfoiioapi.hierarchy.repository;

import io.awportfoiioapi.hierarchy.entity.RoleHierarchy;
import io.awportfoiioapi.hierarchy.repository.query.RoleHierarchyQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long>, RoleHierarchyQueryRepository {
}
