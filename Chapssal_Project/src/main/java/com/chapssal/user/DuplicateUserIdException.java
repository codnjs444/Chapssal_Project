package com.chapssal.user;

// RuntimeException을 상속받은 중복 id로 회원가입시 처리할 에외
public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(String message) {
        super(message);
    }
}
