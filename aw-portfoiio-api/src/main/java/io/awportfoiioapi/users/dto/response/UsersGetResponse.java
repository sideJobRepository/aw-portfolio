package io.awportfoiioapi.users.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class UsersGetResponse {

    private Long id;
    private String companyName;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ipAddress;
    
    @QueryProjection
    public UsersGetResponse(Long id, String companyName, LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt, String ipAddress) {
        this.id = id;
        this.companyName = companyName;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ipAddress = ipAddress;
    }
}
