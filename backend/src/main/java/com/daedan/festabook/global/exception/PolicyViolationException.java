package com.daedan.festabook.global.exception;

import lombok.Getter;

@Getter
public class PolicyViolationException extends BadRequestException {

    public PolicyViolationException(String message) {
        super(message);
    }
}
