package io.awportfoiioapi.file.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.file.repository.query.CommonFileQueryRepository;
import lombok.RequiredArgsConstructor;

import static io.awportfoiioapi.file.entity.QCommonFile.commonFile;

@RequiredArgsConstructor
public class CommonFileRepositoryImpl implements CommonFileQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    @Override
    public CommonFile findByPortfolioFile(Long id, CommonFileType commonFileType) {
        return queryFactory
                .selectFrom(commonFile)
                .where(commonFile.fileTargetId.eq(id),
                        commonFile.fileType.eq(commonFileType)
                ).fetchFirst();
    }
    
    @Override
    public CommonFile findByFileTargetIdAndFileType(Long fileTargetId, CommonFileType fileType) {
        return queryFactory
                .selectFrom(commonFile)
                .where(
                        commonFile.fileTargetId.eq(fileTargetId),
                        commonFile.fileType.eq(fileType)
                )
                .fetchFirst();
    }
}
