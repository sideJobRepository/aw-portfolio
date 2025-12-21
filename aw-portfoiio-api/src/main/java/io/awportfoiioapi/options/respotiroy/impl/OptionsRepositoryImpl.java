package io.awportfoiioapi.options.respotiroy.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.options.respotiroy.query.OptionsQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OptionsRepositoryImpl implements OptionsQueryRepository {

    private final JPAQueryFactory queryFactory;
}
