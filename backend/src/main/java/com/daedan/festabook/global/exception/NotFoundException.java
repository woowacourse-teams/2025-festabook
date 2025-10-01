package com.daedan.festabook.global.exception;

import com.daedan.festabook.global.domain.BaseEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends BusinessException {

    private final Class<? extends BaseEntity> clazz;

    public NotFoundException(Class<? extends BaseEntity> clazz) {
        this(String.format("%s 존재하지 않습니다.", clazz.getSimpleName()), clazz);
    }

    protected NotFoundException(String message, Class<? extends BaseEntity> clazz) {
        super(message, HttpStatus.NOT_FOUND);
        this.clazz = clazz;
    }
}
