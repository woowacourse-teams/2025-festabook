package com.daedan.festabook.global.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDataException extends DatabaseException {

    public DuplicateDataException(String originalExceptionMessage, HttpStatus status) {
        super("중복된 데이터가 발생했습니다.", originalExceptionMessage, status);
    }
}
