package com.daedan.festabook.global.exception;

import com.daedan.festabook.global.domain.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends BusinessException {

    private final Class<?> clazz;

    public ConflictException(Class<? extends BaseEntity> clazz) {
        this(String.format("[%s] 이미 존재합니다.", clazz.getSimpleName()), clazz);
    }

    protected ConflictException(String message, Class<?> clazz) {
        super(message, HttpStatus.CONFLICT);
        this.clazz = clazz;
    }
}
