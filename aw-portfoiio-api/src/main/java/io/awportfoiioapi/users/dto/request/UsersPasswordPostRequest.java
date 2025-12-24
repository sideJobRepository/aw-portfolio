package io.awportfoiioapi.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersPasswordPostRequest {
    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;
    @NotBlank(message = "비밀번호는 필수입력입니다.")
    private String password;
}
