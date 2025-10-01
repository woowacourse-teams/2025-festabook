package com.daedan.festabook.global.exception;

import lombok.Getter;

@Getter
public class DomainValidationException extends BadRequestException {

    public DomainValidationException(String message) {
        super(message);
    }
}
