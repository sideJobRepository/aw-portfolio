package io.awportfoiioapi.file.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.file.repository.query.CommonFileQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommonFileRepositoryImpl implements CommonFileQueryRepository {

    private final JPAQueryFactory queryFactory;
}
