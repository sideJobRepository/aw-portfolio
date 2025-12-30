package io.awportfoiioapi.excel.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.excel.dto.request.ExcelRequest;
import io.awportfoiioapi.excel.dto.response.ExcelColumnResponse;
import io.awportfoiioapi.excel.service.ExcelService;
import io.awportfoiioapi.question.respotiroy.QuestionRepository;
import io.awportfoiioapi.submission.entity.Submission;
import io.awportfoiioapi.submission.repository.SubmissionRepository;
import io.awportfoiioapi.submission.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 1. 컬럼 메타 조회
        List<ExcelColumnResponse> columns =
                questionRepository.findByColumn(portfolioId);

        // 2. 제출 데이터 조회
        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 제출내역입니다."));

        try (Workbook workbook = new XSSFWorkbook()) {

            // JSON -> Map
            Map<String, Object> responseMap =
                    mapper.readValue(
                            submission.getSubmissionJson(),
                            new TypeReference<Map<String, Object>>() {}
                    );

            Sheet sheet = workbook.createSheet("제출 데이터");

            int rowIdx = 0;

            // 헤더/데이터 한 줄씩
            Row headerRow = sheet.createRow(rowIdx++);
            Row dataRow = sheet.createRow(rowIdx++);

            int colIdx = 0;

            for (ExcelColumnResponse col : columns) {

                String optionsType = col.getOptionsType();

                // ===================== 객실 =====================
                if ("PARLOR".equals(optionsType)) {

                    List<Map<String, Object>> rooms =
                            (List<Map<String, Object>>) responseMap.get("rooms");

                    // 헤더
                    headerRow.createCell(colIdx + 0).setCellValue("객실명");
                    headerRow.createCell(colIdx + 1).setCellValue("객실설명");
                    headerRow.createCell(colIdx + 2).setCellValue("형태");
                    headerRow.createCell(colIdx + 3).setCellValue("비수기");
                    headerRow.createCell(colIdx + 4).setCellValue("준성수기");
                    headerRow.createCell(colIdx + 5).setCellValue("성수기");

                    if (rooms != null) {

                        String names = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("name", "")))
                                .collect(Collectors.joining(", "));

                        String descs = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("desc", "")))
                                .collect(Collectors.joining(", "));

                        String types = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("type", "")))
                                .collect(Collectors.joining(", "));

                        String low = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("priceLow", "")))
                                .collect(Collectors.joining(", "));

                        String mid = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("priceMid", "")))
                                .collect(Collectors.joining(", "));

                        String high = rooms.stream()
                                .map(r -> String.valueOf(r.getOrDefault("priceHigh", "")))
                                .collect(Collectors.joining(", "));

                        dataRow.createCell(colIdx + 0).setCellValue(names);
                        dataRow.createCell(colIdx + 1).setCellValue(descs);
                        dataRow.createCell(colIdx + 2).setCellValue(types);
                        dataRow.createCell(colIdx + 3).setCellValue(low);
                        dataRow.createCell(colIdx + 4).setCellValue(mid);
                        dataRow.createCell(colIdx + 5).setCellValue(high);
                    }

                    colIdx += 6;
                    continue;
                }

                // ===================== 스페셜 =====================
                if ("SPECIAL".equals(optionsType)) {

                    List<Map<String, Object>> specials =
                            (List<Map<String, Object>>) responseMap.get("specials");

                    headerRow.createCell(colIdx + 0).setCellValue("스페셜명");
                    headerRow.createCell(colIdx + 1).setCellValue("스페셜설명");

                    if (specials != null) {

                        String names = specials.stream()
                                .map(s -> String.valueOf(s.getOrDefault("name", "")))
                                .collect(Collectors.joining(", "));

                        String descs = specials.stream()
                                .map(s -> String.valueOf(s.getOrDefault("desc", "")))
                                .collect(Collectors.joining(", "));

                        dataRow.createCell(colIdx + 0).setCellValue(names);
                        dataRow.createCell(colIdx + 1).setCellValue(descs);
                    }

                    colIdx += 2;
                    continue;
                }

                // ===================== 환불 =====================
                if ("REFUND".equals(optionsType)) {

                    List<Map<String, Object>> refunds =
                            (List<Map<String, Object>>) responseMap.get("refunds");

                    headerRow.createCell(colIdx + 0).setCellValue("환불기준일");
                    headerRow.createCell(colIdx + 1).setCellValue("환불퍼센트");

                    if (refunds != null) {

                        String days = refunds.stream()
                                .map(r -> String.valueOf(r.getOrDefault("day", "")))
                                .collect(Collectors.joining(", "));

                        String percents = refunds.stream()
                                .map(r -> String.valueOf(r.getOrDefault("percent", "")))
                                .collect(Collectors.joining(", "));

                        dataRow.createCell(colIdx + 0).setCellValue(days);
                        dataRow.createCell(colIdx + 1).setCellValue(percents);
                    }

                    colIdx += 2;
                    continue;
                }

                // ===================== 일반 단답형 =====================

                headerRow.createCell(colIdx).setCellValue(col.getColumn());

                Object val = responseMap.get(String.valueOf(col.getOptionsId()));

                if (val == null) {
                    dataRow.createCell(colIdx++).setCellValue("");
                } else {
                    dataRow.createCell(colIdx++).setCellValue(val.toString());
                }
            }

      
            for (int i = 0; i < colIdx; i++) {
            
                int maxWidth = 0;
            
                int lastRowNum = sheet.getLastRowNum();
            
                for (int r = 0; r <= lastRowNum; r++) {
            
                    Row row = sheet.getRow(r);
                    if (row == null) continue;
            
                    Cell cell = row.getCell(i);
                    if (cell == null) continue;
            
                    String text = cell.toString();
                    if (text == null) continue;
            
                    int visual = 0;
            
                    for (char ch : text.toCharArray()) {
                        // 한글·문자 → 2폭
                        if (ch > 0x007F) {
                            visual += 2;
                        } else {
                            visual += 1;
                        }
                    }
            
                    maxWidth = Math.max(maxWidth, visual);
                }
            
                int width = Math.min(255 * 256, (maxWidth + 2) * 256);
            
                sheet.setColumnWidth(i, width);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("엑셀 생성 실패", e);
        }
    }
    
    @Override
    public ApiResponse modifySubmitOff(ExcelRequest request) {
        Long submissionId = request.getSubmissionId();
        
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("존재하지않는 제출내역입니다."));
        
        submission.modifySubmitOff();
        
        return new ApiResponse(200,true,"제출완료가 취소 되었습니다.");
    }
}
