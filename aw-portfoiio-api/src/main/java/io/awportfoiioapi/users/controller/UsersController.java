package io.awportfoiioapi.users.controller;

import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.member.page.PageResponse;
import io.awportfoiioapi.users.dto.request.UsersPasswordPostRequest;
import io.awportfoiioapi.users.dto.response.UsersGetResponse;
import io.awportfoiioapi.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;
    
    @GetMapping("/members")
    public PageResponse<UsersGetResponse> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        return usersService.getUsers(pageable);
    }
    
    @PostMapping("/members")
    public ApiResponse modifyPassword(@Validated @RequestBody UsersPasswordPostRequest request) {
        return usersService.modifyPassword(request);
    }
    
    @DeleteMapping("/members/{id}")
    public ApiResponse deleteMember(@PathVariable Long id){
        return usersService.deleteUser(id);
    }
}
