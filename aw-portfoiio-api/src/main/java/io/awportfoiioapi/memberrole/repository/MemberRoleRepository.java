package io.awportfoiioapi.memberrole.repository;

import io.awportfoiioapi.memberrole.entity.MemberRole;
import io.awportfoiioapi.memberrole.repository.query.MemberRoleQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long>, MemberRoleQueryRepository {
}
