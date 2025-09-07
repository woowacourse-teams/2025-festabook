package com.daedan.festabook.global.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ConflictExceptionTest {

    @Test
    void 성공_getter() {
        // given
        Class<?> clazz = String.class;
        String expectedMessage = "[String] 이미 존재합니다.";

        // when
        ConflictException conflictException = new ConflictException(clazz);

        // then
        assertAll(() -> assertEquals(expectedMessage, conflictException.getMessage()),
                () -> assertEquals(clazz, conflictException.getClazz()),
                () -> assertEquals(409, conflictException.getStatus().value()));
    }
}
