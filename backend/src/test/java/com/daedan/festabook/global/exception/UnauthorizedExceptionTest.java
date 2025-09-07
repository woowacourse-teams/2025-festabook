package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.global.exception.type.UnauthorizedType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

class UnauthorizedExceptionTest {

    @ParameterizedTest
    @EnumSource(UnauthorizedType.class)
    void 성공_getter(UnauthorizedType unauthorizedType) {
        // when
        UnauthorizedException unauthorizedException = new UnauthorizedException(unauthorizedType);

        // then
        assertSoftly(s -> {
            s.assertThat(unauthorizedException.getUnauthorizedType()).isEqualTo(unauthorizedType);
            s.assertThat(unauthorizedException.getMessage()).isEqualTo(unauthorizedType.getMessage());
            s.assertThat(unauthorizedException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        });
    }
}
