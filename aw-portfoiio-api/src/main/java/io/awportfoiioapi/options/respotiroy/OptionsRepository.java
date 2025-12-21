package io.awportfoiioapi.options.respotiroy;

import io.awportfoiioapi.options.entity.Options;
import io.awportfoiioapi.options.respotiroy.query.OptionsQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionsRepository extends JpaRepository<Options,Long> , OptionsQueryRepository {
}
