package io.awportfoiioapi.excel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExcelRequest {
    
    private Long portfolioId;
    
    private Long submissionId;
}
