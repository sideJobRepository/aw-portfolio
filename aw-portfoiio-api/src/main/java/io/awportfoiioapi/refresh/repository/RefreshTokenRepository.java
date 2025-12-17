package io.awportfoiioapi.refresh.repository;

import io.awportfoiioapi.refresh.entity.RefreshToken;
import io.awportfoiioapi.refresh.repository.query.RefreshTokenQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>, RefreshTokenQueryRepository {
}
