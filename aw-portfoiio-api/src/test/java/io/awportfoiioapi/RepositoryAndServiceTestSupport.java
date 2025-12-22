package io.awportfoiioapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.category.service.CategoryService;
import io.awportfoiioapi.portfolio.serivce.PortfolioService;
import io.awportfoiioapi.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class RepositoryAndServiceTestSupport {
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected CategoryService categoryService;
    
    @Autowired
    protected PortfolioService portfolioService;
    
    @Autowired
    protected QuestionService questionService;
}
