package io.awportfoiioapi.userlist.service.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.userlist.dto.request.UserListPostRequest;
import io.awportfoiioapi.userlist.dto.response.UserListGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class UserListServiceImplTest extends RepositoryAndServiceTestSupport {

    
    @DisplayName("유저 전체 조회")
    @Test
    void test1(){
        PageRequest pageable = PageRequest.of(0, 10);
        
        Page<UserListGetResponse> userList = userListService.getUserList(pageable);
        System.out.println("userList = " + userList);
    }
    
    @DisplayName("")
    @Test
    void test2(){
        
        UserListPostRequest request = new UserListPostRequest("test22", "1234", "테스트용", "ADMIN");
        
        ApiResponse userList = userListService.createUserList(request);
        System.out.println("userList = " + userList);
        
    }
}