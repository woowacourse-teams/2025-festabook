package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ForbiddenExceptionTest {

    @Nested
    class constructor {

        @Test
        void 성공_getter() {
            // given
            String expectedMessage = "권한이 없습니다.";

            // when
            ForbiddenException forbiddenException = new ForbiddenException();

            // then
            assertSoftly(s -> {
                s.assertThat(forbiddenException.getMessage()).isEqualTo(expectedMessage);
                s.assertThat(forbiddenException.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            });
        }
    }
}
