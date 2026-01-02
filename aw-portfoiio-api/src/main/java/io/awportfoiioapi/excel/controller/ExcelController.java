package io.awportfoiioapi.excel.controller;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.excel.dto.request.ExcelRequest;
import io.awportfoiioapi.excel.service.ExcelService;
import io.awportfoiioapi.utils.S3FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExcelController {

    private final ExcelService excelService;
    
    private final S3Client s3Client;
    
     @Value("${spring.cloud.aws.s3.bucket}")
     private String bucketName;
    
    
    @PostMapping("/excel")
    public ResponseEntity<byte[]> getExcel(@RequestBody ExcelRequest request) {
    
    byte[] excelFile = excelService.createSubmissionExcel(request);

    //파일명 (원하면 동적으로 생성해도 됨)
    String fileName = "submission.xlsx";

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .body(excelFile);
    }
    
    @PostMapping("/submitOff")
    public ApiResponse submitOff(@RequestBody ExcelRequest request) {
        return excelService.modifySubmitOff(request);
    }
    
    @GetMapping("/download/{folder}/{fileName}")
             public ResponseEntity<Resource> download(
                     @PathVariable String fileName,
                     @PathVariable String folder
             ) {
                 String key = folder + "/" + fileName;
                 ResponseInputStream<GetObjectResponse> object = s3Client.getObject(
                         GetObjectRequest.builder()
                                 .bucket(bucketName)
                                 .key(key)
                                 .build()
                 );
                 
                 // 메타데이터에서 원본 파일명 가져오기
                 String encodedFilenameInMetadata = object.response().metadata().get("original-filename");
                 
                 // 원래 이름 복원 (디코딩)
                 String decodedFilename = encodedFilenameInMetadata != null
                         ? URLDecoder.decode(encodedFilenameInMetadata, StandardCharsets.UTF_8)
                         : fileName;
                 
                 // 다시 Content-Disposition용으로 인코딩 (한 번만)
                 String encodedFilename = UriUtils.encode(decodedFilename, StandardCharsets.UTF_8);
                 String contentDisposition = "attachment; filename*=UTF-8''" + encodedFilename;
                 
                 InputStreamResource resource = new InputStreamResource(object);
                 return ResponseEntity.ok()
                         .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                         .contentType(MediaType.APPLICATION_OCTET_STREAM)
                         .body(resource);
             }
}
