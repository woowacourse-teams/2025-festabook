package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
        assertSoftly(s -> {
            s.assertThat(notFoundException.getClazz()).isEqualTo(clazz);
            s.assertThat(notFoundException.getMessage()).isEqualTo(expectedMessage);
            s.assertThat(notFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }
}
