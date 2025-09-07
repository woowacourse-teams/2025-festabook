package com.daedan.festabook.global.exception.type;

import lombok.Getter;

@Getter
public enum ForbiddenType {

    NOT_OWNER("다른 소유자의 리소스는 접근/수정할 수 없습니다."),
    NO_AUTHORITY("권한이 없습니다.");

    private final String message;

    ForbiddenType(String message) {
        this.message = message;
    }
}
