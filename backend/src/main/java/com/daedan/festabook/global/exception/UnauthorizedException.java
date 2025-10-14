package com.daedan.festabook.global.exception;

import com.daedan.festabook.global.exception.type.UnauthorizedType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedException extends BusinessException {

    private final UnauthorizedType unauthorizedType;

    public UnauthorizedException(UnauthorizedType unauthorizedType) {
        super(unauthorizedType.getMessage(), HttpStatus.UNAUTHORIZED);
        this.unauthorizedType = unauthorizedType;
    }
}
