package io.awportfoiioapi.file.repository.query;

import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;

public interface CommonFileQueryRepository {
    
    
    CommonFile findByPortfolioFile(Long id, CommonFileType commonFileType);
    
    CommonFile findByFileTargetIdAndFileType(Long fileTargetId, CommonFileType fileType);
    
}
