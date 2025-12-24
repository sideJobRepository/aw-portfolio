package io.awportfoiioapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awportfoiioapi.category.service.CategoryService;
import io.awportfoiioapi.portfolio.serivce.PortfolioService;
import io.awportfoiioapi.question.service.QuestionService;
import io.awportfoiioapi.submission.service.SubmissionService;
import io.awportfoiioapi.submissions.serivce.SubmissionsService;
import io.awportfoiioapi.userlist.service.UserListService;
import io.awportfoiioapi.users.service.UsersService;
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
    
    @Autowired
    protected SubmissionService submissionService;
    
    @Autowired
    protected UserListService userListService;
    
    @Autowired
    protected UsersService usersService;
    
    @Autowired
    protected SubmissionsService submissionsService;
}
