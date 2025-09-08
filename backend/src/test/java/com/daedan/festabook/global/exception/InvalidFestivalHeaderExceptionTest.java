package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InvalidFestivalHeaderExceptionTest {

    @Nested
    class constructor {

        @Test
        void 성공_getter() {
            // given
            String message = "Festival 헤더가 누락되었습니다.";

            // when
            InvalidFestivalHeaderException invalidFestivalHeaderException = new InvalidFestivalHeaderException(message);

            // then
            assertSoftly(s -> {
                s.assertThat(invalidFestivalHeaderException.getMessage()).isEqualTo(message);
                s.assertThat(invalidFestivalHeaderException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            });
        }
    }
}
