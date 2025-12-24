package io.awportfoiioapi.file.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.entity.QCommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.file.repository.query.CommonFileQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.awportfoiioapi.file.entity.QCommonFile.*;
import static io.awportfoiioapi.file.entity.QCommonFile.commonFile;

@RequiredArgsConstructor
public class CommonFileRepositoryImpl implements CommonFileQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    
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
    
    @Override
    public List<CommonFile> findByFileTargetIdAndFileTypeList(Long fileTargetId, CommonFileType fileType) {
        return queryFactory
                .selectFrom(commonFile)
                .where(
                        commonFile.fileTargetId.eq(fileTargetId),
                        commonFile.fileType.eq(fileType)
                )
                .fetch();
    }
    
    @Override
    public Long deleteByTargetIdAndType(Long id, CommonFileType commonFileType) {
        em.flush();
        return queryFactory
                .delete(commonFile)
                .where(
                        commonFile.fileTargetId.eq(id),
                        commonFile.fileType.eq(commonFileType)
                )
                .execute();
    }
    
    @Override
    public void deleteSubmissionOptionFiles(Long submissionId, CommonFileType commonFileType) {
        em.flush();
        queryFactory
                .delete(commonFile)
                .where(
                        commonFile.fileTargetId.eq(submissionId),
                        commonFile.fileType.eq(commonFileType)
                )
                .execute();
    }
    
    @Override
    public List<CommonFile> findBySubmissions(List<Long> ids) {
        return queryFactory
                .selectFrom(commonFile)
                .where(commonFile.fileTargetId.in(ids))
                .fetch();
    }
    
    @Override
    public void deleteBySubmissionsFile(List<Long> ids) {
        em.flush();
        queryFactory
                .delete(commonFile)
                .where(commonFile.fileTargetId.in(ids))
                .execute();
    }
}
