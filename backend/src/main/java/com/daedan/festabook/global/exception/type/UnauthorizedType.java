package com.daedan.festabook.global.exception.type;

import lombok.Getter;

@Getter
public enum UnauthorizedType {

    BAD_CREDENTIALS("아이디/비밀번호가 올바르지 않습니다."),
    NO_TOKEN("토큰이 없습니다."),
    EXPIRED_TOKEN("토큰이 만료되었습니다."),
    INVALID_TOKEN("토큰이 유효하지 않습니다.");

    private final String message;

    UnauthorizedType(String message) {
        this.message = message;
    }
}
