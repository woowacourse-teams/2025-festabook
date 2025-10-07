package com.daedan.festabook.global.exception;

import lombok.Getter;

@Getter
public class InvalidFestivalHeaderException extends BadRequestException {

    public InvalidFestivalHeaderException(String message) {
        super(message);
    }
}
