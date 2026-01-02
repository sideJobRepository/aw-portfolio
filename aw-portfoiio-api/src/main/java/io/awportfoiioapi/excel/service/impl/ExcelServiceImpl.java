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
                            new TypeReference<>() {
                            }
                    );
            
            Sheet sheet = workbook.createSheet("제출 데이터");
            
            int rowIdx = 0;
            
            // 헤더/데이터 한 줄씩
            Row headerRow = sheet.createRow(rowIdx++);
            Row dataRow = sheet.createRow(rowIdx++);
            
            int colIdx = 0;
            
            for (ExcelColumnResponse col : columns) {
                
                String optionsType = col.getOptionsType();
                if ("AGREEMENT".equals(optionsType)) {
                    continue;
                }
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
                    
                    // 컬럼 1개만 사용
                    headerRow.createCell(colIdx).setCellValue("환불기준 및 퍼센트");
                    
                    if (refunds != null) {
                        
                        String refundTexts = refunds.stream()
                                .map(r -> {
                                    
                                    String id = String.valueOf(r.getOrDefault("id", ""));
                                    String day = String.valueOf(r.getOrDefault("day", ""));
                                    String percent = String.valueOf(r.getOrDefault("percent", ""));
                                    
                                    String base;
                                    
                                    if ("refund-1".equals(id)) {
                                        // 방문 당일
                                        base = "방문당일 총 금액의";
                                    } else {
                                        // n일 전
                                        base = "방문 " + day + "일 전 총 금액의";
                                    }
                                    
                                    // => 방문당일 총 금액의 20% 환불
                                    // => 방문 1일 전 총 금액의 30% 환불
                                    return base + " " + percent + "% 환불";
                                })
                                .collect(Collectors.joining(", ")); // , 로 연결
                        
                        dataRow.createCell(colIdx).setCellValue(refundTexts);
                    }
                    colIdx += 1;
                    continue;
                }
                
                // ===================== 체크박스 =====================
                if ("CHECKBOX".equals(optionsType)) {
                    
                    Map<String, Object> checkbox =
                            (Map<String, Object>) responseMap.get(String.valueOf(col.getOptionsId()));
                    
                    headerRow.createCell(colIdx).setCellValue(col.getColumn());
                    
                    if (checkbox != null) {
                        
                        String selected = String.valueOf(checkbox.getOrDefault("selected", ""));
                        
                        Map<String, Object> inputs =
                                (Map<String, Object>) checkbox.get("inputs");
                        
                        String input = "";
                        if (inputs != null) {
                            input = String.valueOf(inputs.getOrDefault(selected, ""));
                        }
                        
                        // 결과 예시: "선택지 2 (ㅎㅎ)"
                        String text = selected;
                        
                        if (!input.isBlank()) {
                            text += " (" + input + ")";
                        }
                        
                        dataRow.createCell(colIdx).setCellValue(text);
                        
                    } else {
                        dataRow.createCell(colIdx).setCellValue("");
                    }
                    
                    colIdx++;
                    continue;
                }
                
                // ===================== 멀티 텍스트 =====================
                if ("MULTI_TEXT".equals(optionsType)) {
                
                    headerRow.createCell(colIdx).setCellValue(col.getColumn());
                
                    Object value = responseMap.get(String.valueOf(col.getOptionsId()));
                
                    if (value instanceof List) {
                
                        List<Object> list = (List<Object>) value;
                
                        String joined = list.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")); //대괄호 없이 ,로
                
                        dataRow.createCell(colIdx).setCellValue(joined);
                
                    } else if (value != null) {
                        // 혹시 배열이 아닌 단일 값이 들어온 경우
                        dataRow.createCell(colIdx).setCellValue(value.toString());
                    } else {
                        dataRow.createCell(colIdx).setCellValue("");
                    }
                
                    colIdx++;
                    continue;
                }
                
                
                // ===================== 일반 단답형 =====================
                
                headerRow.createCell(colIdx).setCellValue(col.getColumn());
                
                Object value = responseMap.get(String.valueOf(col.getOptionsId()));
                
                if (value == null) {
                    dataRow.createCell(colIdx++).setCellValue("");
                } else {
                    dataRow.createCell(colIdx++).setCellValue(value.toString());
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
        
        return new ApiResponse(200, true, "제출완료가 취소 되었습니다.");
    }
}
