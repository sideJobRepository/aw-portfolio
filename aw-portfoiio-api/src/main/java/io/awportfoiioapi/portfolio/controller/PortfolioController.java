package io.awportfoiioapi.portfolio.controller;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.page.PageResponse;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPostRequest;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPutRequest;
import io.awportfoiioapi.portfolio.dto.response.PortfolioGetDetailResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfolioResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfoliosGetDetailResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfoliosOneGetResponse;
import io.awportfoiioapi.portfolio.serivce.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PortfolioController {
    
    private final PortfolioService portfolioService;
    
    @GetMapping("/portfolio")
    public PageResponse<PortfolioResponse> getPortfolioList(@PageableDefault(size = 10) Pageable pageable) {
        Page<PortfolioResponse> portfolioList = portfolioService.getPortfolioList(pageable);
        return PageResponse.from(portfolioList);
    }
    
    @GetMapping("/portfolio/{id}")
    public PortfolioGetDetailResponse getPortfolioDetail(@PathVariable Long id) {
        return portfolioService.getPortfolioDetail(id);
    }
    
    
    @GetMapping("/portfolios")
    public List<PortfolioResponse> getPortfolios(@RequestParam(required = false) Boolean active,
                                                 @RequestParam(required = false) Long categoryId
    ) {
        return portfolioService.getPortfolioList(active, categoryId);
    }
    
    @GetMapping("/portfolios/{id}")
    public List<PortfoliosGetDetailResponse> getPortfoliosDetail(@PathVariable Long id) {
        return portfolioService.getPortfolioDetailOptions(id);
    }
    
    @GetMapping("/portfolios/{id}/one")
    public PortfoliosOneGetResponse getOnePortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolioOneDetail(id);
    }
    
    @PostMapping("/portfolio")
    public ApiResponse createPortfolio(@Validated @ModelAttribute PortfolioPostRequest request) {
        return portfolioService.createPortfolio(request);
    }
    
    @PutMapping("/portfolio")
    public ApiResponse createPortfolio(@Validated @ModelAttribute PortfolioPutRequest request) {
        return portfolioService.modifyPortfolio(request);
    }
    
    @DeleteMapping("/portfolio/{id}")
    public ApiResponse deletePortfolio(@PathVariable Long id) {
        return portfolioService.deletePortfolio(id);
    }
    
}
