package io.awportfoiioapi.file.repository;

import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.repository.query.CommonFileQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonFileRepository extends JpaRepository<CommonFile,Long>, CommonFileQueryRepository {
}
