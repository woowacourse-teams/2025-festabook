package com.daedan.festabook.global.exception;

import lombok.Getter;

@Getter
public class InvalidFileException extends BadRequestException {

    public InvalidFileException(String message) {
        super(message);
    }
}
