package com.daedan.festabook.global.exception;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
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
            Class clazz = BaseEntity.class;
            String expectedMessage = "[BaseEntity] 이미 존재합니다.";

            // when
            ConflictException conflictException = new ConflictException(clazz);

            // then
            assertSoftly(s -> {
                s.assertThat(conflictException.getClazz()).isEqualTo(clazz);
                s.assertThat(conflictException.getMessage()).isEqualTo(expectedMessage);
                s.assertThat(conflictException.getStatus()).isEqualTo(HttpStatus.CONFLICT);
            });
        }

        @Test
        void 성공_자식타입_확인() {
            // given
            Class clazz = Festival.class;
            String expectedMessage = "[Festival] 이미 존재합니다.";

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
