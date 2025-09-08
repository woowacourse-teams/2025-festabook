package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.global.exception.type.ForbiddenType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ForbiddenExceptionTest {

    @Nested
    class constructor {

        @ParameterizedTest
        @EnumSource(ForbiddenType.class)
        void 성공_getter(ForbiddenType forbiddenType) {
            // when
            ForbiddenException forbiddenException = new ForbiddenException(forbiddenType);

            // then
            assertSoftly(s -> {
                s.assertThat(forbiddenException.getForbiddenType()).isEqualTo(forbiddenType);
                s.assertThat(forbiddenException.getMessage()).isEqualTo(forbiddenType.getMessage());
                s.assertThat(forbiddenException.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
            });
        }
    }
}
