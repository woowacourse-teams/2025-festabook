package com.daedan.festabook.global.exception;

import com.daedan.festabook.global.exception.type.ForbiddenType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends BusinessException {

    private final ForbiddenType forbiddenType;

    public ForbiddenException(ForbiddenType forbiddenType) {
        super(forbiddenType.getMessage(), HttpStatus.FORBIDDEN);
        this.forbiddenType = forbiddenType;
    }
}
