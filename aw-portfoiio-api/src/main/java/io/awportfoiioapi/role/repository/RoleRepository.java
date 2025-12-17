package io.awportfoiioapi.role.repository;

import io.awportfoiioapi.role.entity.Role;
import io.awportfoiioapi.role.repository.query.RoleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> , RoleQueryRepository {
}
