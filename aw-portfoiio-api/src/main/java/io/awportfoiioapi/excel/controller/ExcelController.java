package io.awportfoiioapi.excel.controller;

import io.awportfoiioapi.excel.dto.request.ExcelRequest;
import io.awportfoiioapi.excel.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExcelController {

    private final ExcelService excelService;
    
    
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
}
