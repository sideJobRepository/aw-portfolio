package io.awportfoiioapi.question.respotiroy.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.question.respotiroy.query.QuestionQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionQueryRepository {

    private final JPAQueryFactory queryFactory;
}
