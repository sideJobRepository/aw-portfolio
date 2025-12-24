package io.awportfoiioapi.submissions.serivce.impl;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.repository.CommonFileRepository;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import io.awportfoiioapi.submissions.dto.response.SubmissionsGetRequest;
import io.awportfoiioapi.submissions.serivce.SubmissionsService;
import io.awportfoiioapi.utils.S3FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionsServiceImpl implements SubmissionsService {
    
    private final SubmissionRepository submissionRepository;
    
    private final CommonFileRepository commonFileRepository;
    
    private final S3FileUtils s3FileUtils;
    
    @Override
    public Page<SubmissionsGetRequest> getSubmissions(Pageable pageable) {
        return submissionRepository.findByAdminSubmissions(pageable);
    }
    
    @Override
    public ApiResponse deleteSubmission(Long id) {
        Optional<Submission> submission = submissionRepository.findById(id);
        if (submission.isPresent()) {
            List<Long> ids = List.of(submission.get().getId());
            List<CommonFile> submissionFiles = commonFileRepository.findBySubmissions(ids);
            if (!submissionFiles.isEmpty()) {
                for (CommonFile file : submissionFiles) {
                    s3FileUtils.deleteFile(file.getFileUrl());
                }
                commonFileRepository.deleteBySubmissionsFile(ids);
            }
            submissionRepository.deleteBySubmissions(id);
        }
        return new ApiResponse(200,true,"포트폴리오가 삭제되었습니다.");
    }
}
