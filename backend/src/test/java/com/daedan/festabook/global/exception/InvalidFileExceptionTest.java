package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InvalidFileExceptionTest {

    @Test
    void 성공_getter() {
        // given
        String message = "파일은 비어있을 수 없습니다.";

        // when
        InvalidFileException invalidFileException = new InvalidFileException(message);

        // then
        assertSoftly(s -> {
            s.assertThat(invalidFileException.getMessage()).isEqualTo(message);
            s.assertThat(invalidFileException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        });
    }
}
