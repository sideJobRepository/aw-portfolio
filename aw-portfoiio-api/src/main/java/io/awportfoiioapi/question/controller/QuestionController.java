package io.awportfoiioapi.question.controller;


import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.category.dto.request.CategoryPostRequest;
import io.awportfoiioapi.category.dto.request.CategoryPutRequest;
import io.awportfoiioapi.category.dto.response.CategoryGetResponse;
import io.awportfoiioapi.member.page.PageResponse;
import io.awportfoiioapi.question.dto.request.QuestionPostRequest;
import io.awportfoiioapi.question.dto.request.QuestionPutRequest;
import io.awportfoiioapi.question.dto.response.QuestionGetResponse;
import io.awportfoiioapi.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class QuestionController {
    
    private final QuestionService questionService;
    
    
    @GetMapping("/question/{id}")
    public List<QuestionGetResponse> getCategoryList(@PathVariable Long id) {
        return questionService.getQuestion(id);
    }
    
    @PostMapping("/question")
    public ApiResponse createCategory(@Validated @ModelAttribute QuestionPostRequest request) {
        return questionService.createQuestion(request);
    }
    
    @PutMapping("/question")
    public ApiResponse modifyCategory(@Validated @ModelAttribute QuestionPutRequest request) {
        return questionService.modifyQuestion(request);
    }
    
    @DeleteMapping("/question/{id}")
    public ApiResponse deleteCategory(@PathVariable Long id) {
        return questionService.deleteQuestion(id);
    }
}
