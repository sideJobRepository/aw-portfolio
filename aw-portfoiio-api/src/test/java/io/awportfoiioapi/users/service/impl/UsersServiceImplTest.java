package io.awportfoiioapi.users.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.users.dto.request.UsersPasswordPostRequest;
import io.awportfoiioapi.users.dto.response.UsersGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

class UsersServiceImplTest extends RepositoryAndServiceTestSupport {
    
    
    @DisplayName("어드민 회원목록 조회")
    @Test
    void test1(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        
        Page<UsersGetResponse> users = usersService.getUsers(pageRequest);
        
        System.out.println("users = " + users);
    }
    
    @DisplayName("어드민 회원 비밀번호 변경")
    @Test
    void test2(){
        
        UsersPasswordPostRequest request = new UsersPasswordPostRequest(10L, "4567");
        ApiResponse apiResponse = usersService.modifyPassword(request);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("어드민 회원 삭제")
    @Test
    void test3(){
        ApiResponse apiResponse = usersService.deleteUser(10L);
        System.out.println("apiResponse = " + apiResponse);
    }
}