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
      
          // 1. 엑셀 컬럼 정의 조회
          List<ExcelColumnResponse> columns = questionRepository.findByColumn(portfolioId);
      
          // 2. 제출 데이터 조회
          Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("존재하지않는 제출내역입니다."));
      
          try (Workbook workbook = new XSSFWorkbook()) {
      
              // 3. JSON → Map
              Map<String, Object> responseMap =
                      mapper.readValue(
                              submission.getSubmissionJson(),
                              new TypeReference<>() {}
                      );
      
              Sheet sheet = workbook.createSheet("제출데이터");
      
              int rowIdx = 0;
      
              // 4. 헤더 생성
              Row headerRow = sheet.createRow(rowIdx++);
              int headerCellIdx = 0;
      
              for (ExcelColumnResponse col : columns) {
                  Cell cell = headerRow.createCell(headerCellIdx++);
                  cell.setCellValue(col.getColumn());
              }
      
              // 5. 데이터 ROW 생성
              Row dataRow = sheet.createRow(rowIdx++);
              int dataCellIdx = 0;
      
              for (ExcelColumnResponse col : columns) {
      
                  Cell cell = dataRow.createCell(dataCellIdx++);
      
                  // optionsType 기준으로 JSON key 매핑
                  String key;
      
                  switch (col.getOptionsType()) {
                      case "PARLOR":      // 객실
                          key = "rooms";
                          break;
                      case "SPECIAL":     // 스페셜
                          key = "specials";
                          break;
                      case "REFUND":      // 환불
                          key = "refunds";
                          break;
                      default:            // 일반 질문
                          key = String.valueOf(col.getOptionsId());
                  }
      
                  Object value = responseMap.get(key);
      
                  // 값이 없으면 빈칸
                  if (value == null) {
                      cell.setCellValue("");
                      continue;
                  }
      
                  // 문자열 값
                  if (value instanceof String s) {
                      cell.setCellValue(s);
                      continue;
                  }
      
                  // 리스트 값 (rooms, specials, refunds 등)
                  // 한 셀에 "A, B, C" 형태로 출력
                  if (value instanceof List<?> list) {
      
                      String result = list.stream()
                              .map(obj -> {
                                  // JSON Object인 경우
                                  if (obj instanceof Map<?, ?> m) {
      
                                      // name 필드 우선 사용
                                      Object name = m.get("name");
                                      if (name != null) return name.toString();
      
                                      // title 있으면 fallback
                                      Object title = m.get("title");
                                      if (title != null) return title.toString();
      
                                      // id라도 반환
                                      Object id = m.get("id");
                                      return id != null ? id.toString() : "";
                                  }
      
                                  // 단순 배열 값
                                  return obj.toString();
                              })
                              .filter(s -> s != null && !s.isBlank())
                              .collect(Collectors.joining(", ")); // 콤마 구분
      
                      cell.setCellValue(result);
                      continue;
                  }
      
                  // ------------------------
                  // Map 자체인 경우 (객체형 응답)
                  // ------------------------
                  if (value instanceof Map<?, ?> mapObj) {
                      cell.setCellValue(mapObj.toString());
                      continue;
                  }
      
                  // ------------------------
                  // 숫자, boolean 등 기본 타입
                  // ------------------------
                  cell.setCellValue(value.toString());
              }
      
              // 6. 엑셀 → byte[]
              ByteArrayOutputStream out = new ByteArrayOutputStream();
              workbook.write(out);
      
              return out.toByteArray();
      
          } catch (Exception e) {
              throw new RuntimeException("엑셀 생성 실패", e);
          }
    }
}
