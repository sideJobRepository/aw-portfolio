package io.awportfoiioapi.excel.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.excel.dto.request.ExcelRequest;
import io.awportfoiioapi.excel.dto.response.ExcelColumnResponse;
import io.awportfoiioapi.excel.service.ExcelService;
import io.awportfoiioapi.question.respotiroy.QuestionRepository;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {
    
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final ObjectMapper mapper;
    
    @Override
    public byte[] createSubmissionExcel(ExcelRequest request) {
        
        Long portfolioId = request.getPortfolioId();
        Long submissionId = request.getSubmissionId();
        
        List<ExcelColumnResponse> columns = questionRepository.findByColumn(portfolioId);
        
        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() -> new RuntimeException("존재하지않는 제출내역입니다."));
        
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // JSON -> Map 변환
            Map<String, Object> responseMap =
                    mapper.readValue(
                            submission.getSubmissionJson(), new TypeReference<>() {}
                    );
            
            Sheet sheet = workbook.createSheet("제출데이터");
            
            int rowIdx = 0;
            
            // 헤더 (컬럼명)
            Row headerRow = sheet.createRow(rowIdx++);
            int headerCellIdx = 0;
            
            for (ExcelColumnResponse col : columns) {
                Cell cell = headerRow.createCell(headerCellIdx++);
                cell.setCellValue(col.getColumn());
            }
            
            //데이터 ROW (제출값)
            Row dataRow = sheet.createRow(rowIdx++);
            int dataCellIdx = 0;
            
            for (ExcelColumnResponse col : columns) {
                
                Cell cell = dataRow.createCell(dataCellIdx++);
                
                // questionId 기준으로 JSON 값 조회
                String key = String.valueOf(col.getOptionsId());
                
                Object value = responseMap.get(key);
                
                if (value == null) {
                    cell.setCellValue("");
                    continue;
                }
                
                // 문자열 값
                if (value instanceof String s) {
                    cell.setCellValue(s);
                    continue;
                }
                
                // 숫자, boolean 등
                cell.setCellValue(value.toString());
            }
            
            //엑셀을 byte[] 로 변환
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            
            return out.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("엑셀 생성 실패", e);
        }
    }
}
