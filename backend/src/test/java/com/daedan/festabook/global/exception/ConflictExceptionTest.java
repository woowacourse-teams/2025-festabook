package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ConflictExceptionTest {

    @Nested
    class constructor {

        @Test
        void 성공_getter() {
            // given
            Class<?> clazz = String.class;
            String expectedMessage = "[String] 이미 존재합니다.";

            // when
            ConflictException conflictException = new ConflictException(clazz);

            // then
            assertSoftly(s -> {
                s.assertThat(conflictException.getClazz()).isEqualTo(clazz);
                s.assertThat(conflictException.getMessage()).isEqualTo(expectedMessage);
                s.assertThat(conflictException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
            });
        }
    }
}
