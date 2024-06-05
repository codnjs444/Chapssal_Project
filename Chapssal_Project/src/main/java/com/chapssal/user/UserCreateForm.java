package com.chapssal.user;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UserCreateForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String userId;
    
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password1;
    
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "학교 코드는 필수 항목입니다.")
    private String schoolCode;

}
