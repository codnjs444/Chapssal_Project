package com.chapssal.user;

// 사용자와 관련된 DTO (사용자가 학교 코드를 입력할 때 사용)
// school 패키지가 아닌, user 패키지에 추가

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolCodeForm {
    @NotEmpty(message = "학교 코드를 입력하세요.")
    private String schoolCode;
}
