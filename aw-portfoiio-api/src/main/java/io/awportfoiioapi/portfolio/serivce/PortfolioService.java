package io.awportfoiioapi.portfolio.serivce;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPostRequest;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPutRequest;
import io.awportfoiioapi.portfolio.dto.response.PortfolioGetDetailResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfolioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PortfolioService {
    Page<PortfolioResponse> getPortfolioList(Pageable pageable);
    PortfolioGetDetailResponse getPortfolioDetail(Long id);
    ApiResponse createPortfolio(PortfolioPostRequest request);
    ApiResponse modifyPortfolio(PortfolioPutRequest request);
    ApiResponse deletePortfolio(Long id);
}
