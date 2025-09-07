package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class DomainValidationExceptionTest {

    @Test
    void 성공_getter() {
        // given
        String message = "공지사항 본문은 비어 있을 수 없습니다.";

        // when
        DomainValidationException domainValidationException = new DomainValidationException(message);

        // then
        assertSoftly(s -> {
            s.assertThat(domainValidationException.getMessage()).isEqualTo(message);
            s.assertThat(domainValidationException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        });
    }
}
