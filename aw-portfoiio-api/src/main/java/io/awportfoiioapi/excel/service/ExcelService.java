package io.awportfoiioapi.excel.service;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.excel.dto.request.ExcelRequest;

import java.util.List;

public interface ExcelService {
    
    byte[] createSubmissionExcel(ExcelRequest requests);
    
    ApiResponse modifySubmitOff(ExcelRequest request);
}
