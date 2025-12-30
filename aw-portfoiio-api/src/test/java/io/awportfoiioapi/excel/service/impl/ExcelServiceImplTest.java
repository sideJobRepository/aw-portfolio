package io.awportfoiioapi.excel.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.excel.dto.request.ExcelRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceImplTest extends RepositoryAndServiceTestSupport {
    
    
    @DisplayName("")
    @Test
    void test1() {
        
        Long portfolioId = 12L;
        Long submissionId = 39L;
        ExcelRequest excelRequest = new ExcelRequest(portfolioId,submissionId);
        byte[] submissionExcel = excelService.createSubmissionExcel(excelRequest);
        System.out.println("submissionExcel = " + submissionExcel);
        
    }
}