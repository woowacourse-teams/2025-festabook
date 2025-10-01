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
class NotFoundExceptionTest {

    @Nested
    class constructor {

        @Test
        void 성공_getter() {
            // given
            Class clazz = BaseEntity.class;
            String expectedMessage = "BaseEntity 존재하지 않습니다.";

            // when
            NotFoundException notFoundException = new NotFoundException(clazz);

            // then
            assertSoftly(s -> {
                s.assertThat(notFoundException.getClazz()).isEqualTo(clazz);
                s.assertThat(notFoundException.getMessage()).isEqualTo(expectedMessage);
                s.assertThat(notFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            });
        }

        @Test
        void 성공_자식타입_확인() {
            // given
            Class clazz = Festival.class;
            String expectedMessage = "Festival 존재하지 않습니다.";

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
}
