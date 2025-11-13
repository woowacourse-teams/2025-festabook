package com.daedan.festabook.global.exception;

import com.daedan.festabook.global.domain.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UniqueDuplicateDataException extends DatabaseException {

    private final Class<? extends BaseEntity> clazz;

    public UniqueDuplicateDataException(Class<? extends BaseEntity> clazz, String originalExceptionMessage) {
        this(String.format("%s 데이터베이스에 이미 존재합니다.", clazz.getSimpleName()), clazz, originalExceptionMessage);
    }

    protected UniqueDuplicateDataException(String message, Class<? extends BaseEntity> clazz,
                                           String originalExceptionMessage) {
        super(message, originalExceptionMessage, HttpStatus.CONFLICT);
        this.clazz = clazz;
    }
}
