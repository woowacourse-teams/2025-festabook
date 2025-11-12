package com.daedan.festabook.global.exception;

public class UniqueDuplicateDataException extends DuplicateDataException {

    public UniqueDuplicateDataException(String originalExceptionMessage) {
        super("중복된 데이터 삽입이 발생했습니다.", originalExceptionMessage);
    }
}
