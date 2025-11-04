package com.daedan.festabook.global.exception;

public class DuplicateDataException extends DatabaseException {

    public DuplicateDataException(String originalMessage) {
        super("중복된 데이터가 발생했습니다.", originalMessage);
    }
}
