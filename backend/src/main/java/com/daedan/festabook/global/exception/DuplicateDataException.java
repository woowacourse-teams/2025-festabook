package com.daedan.festabook.global.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDataException extends DatabaseException {

    protected DuplicateDataException(String message, String originalExceptionMessage) {
        super(message, originalExceptionMessage, HttpStatus.CONFLICT);
    }
}
