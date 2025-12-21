package io.awportfoiioapi.question.respotiroy;

import io.awportfoiioapi.question.entity.Question;
import io.awportfoiioapi.question.respotiroy.query.QuestionQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> , QuestionQueryRepository {
}
