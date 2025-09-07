package com.daedan.festabook.global.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NotFoundExceptionTest {

    @Test
    void 성공_getter() {
        // given
        Class<?> clazz = String.class;
        String expectedMessage = "[String] 존재하지 않습니다.";

        // when
        NotFoundException notFoundException = new NotFoundException(clazz);

        // then
        assertAll(
                () -> assertEquals(expectedMessage, notFoundException.getMessage()),
                () -> assertEquals(clazz, notFoundException.getClazz()),
                () -> assertEquals(404, notFoundException.getStatus().value())
        );
    }
}
