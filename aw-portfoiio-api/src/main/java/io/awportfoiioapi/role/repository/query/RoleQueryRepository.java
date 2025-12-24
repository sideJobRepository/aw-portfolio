package io.awportfoiioapi.role.repository.query;

import io.awportfoiioapi.role.entity.Role;

public interface RoleQueryRepository  {
    Role findByRoleName(String role);
}
