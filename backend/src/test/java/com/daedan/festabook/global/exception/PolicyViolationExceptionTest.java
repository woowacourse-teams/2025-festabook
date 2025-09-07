package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PolicyViolationExceptionTest {

    @Test
    void 성공_getter() {
        // given
        String message = "축제 기간이 아닌 기간에 일정을 생성할 수 없습니다.";

        // when
        PolicyViolationException policyViolationException = new PolicyViolationException(message);

        // then
        assertSoftly(s -> {
            s.assertThat(policyViolationException.getMessage()).isEqualTo(message);
            s.assertThat(policyViolationException.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        });
    }
}
