package io.awportfoiioapi.file.repository.query;

import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;

import java.util.List;

public interface CommonFileQueryRepository {
    
    
    CommonFile findByPortfolioFile(Long id, CommonFileType commonFileType);
    
    CommonFile findByFileTargetIdAndFileType(Long fileTargetId, CommonFileType fileType);
    
    List<CommonFile> findByFileTargetIdAndFileTypeList(Long fileTargetId, CommonFileType fileType);
    
    Long deleteByTargetIdAndType(Long id, CommonFileType commonFileType);
    
    void deleteSubmissionOptionFiles(Long submissionId,CommonFileType commonFileType);
    
    List<CommonFile> findBySubmissions(List<Long> ids);
    
    void deleteBySubmissionsFile(List<Long> ids);
}
