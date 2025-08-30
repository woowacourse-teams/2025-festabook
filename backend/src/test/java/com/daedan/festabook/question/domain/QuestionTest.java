package com.daedan.festabook.question.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QuestionTest {

    @Nested
    class validateFestival {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();

            // when & then
            assertThatCode(() -> QuestionFixture.create(festival))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_축제_null() {
            // given
            Festival festival = null;

            // when & then
            assertThatThrownBy(() -> QuestionFixture.create(festival))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Festival은 null일 수 없습니다.");
        }
    }

    @Nested
    class validateQuestion {

        @Test
        void 성공_경계값() {
            // given
            int maxQuestionLength = 500;
            String question = "미".repeat(maxQuestionLength);

            // when & then
            assertThatCode(() -> QuestionFixture.createWithQuestion(question))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_질문_null() {
            // given
            String question = null;

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithQuestion(question))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("질문은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_질문_blank() {
            // given
            String question = " ";

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithQuestion(question))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("질문은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_질문_길이_초과() {
            // given
            int maxQuestionLength = 500;
            String question = "미".repeat(maxQuestionLength + 1);

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithQuestion(question))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("질문은 500자를 초과할 수 없습니다.");
        }
    }

    @Nested
    class validateAnswer {

        @Test
        void 성공_경계값() {
            // given
            int maxAnswerLength = 1000;
            String answer = "미".repeat(maxAnswerLength);

            // when & then
            assertThatCode(() -> QuestionFixture.createWithAnswer(answer))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_답변_null() {
            // given
            String answer = null;

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithAnswer(answer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_답변_blank() {
            // given
            String answer = " ";

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithAnswer(answer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_답변_길이_초과() {
            // given
            int maxAnswerLength = 1000;
            String answer = "미".repeat(maxAnswerLength + 1);

            // when & then
            assertThatThrownBy(() -> QuestionFixture.createWithAnswer(answer))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 1000자를 초과할 수 없습니다.");
        }
    }

    @Nested
    class isFestivalIdEqualTo {

        @Test
        void 같은_축제의_id이면_true() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Question question = QuestionFixture.create(festival);

            // when
            boolean result = question.isFestivalIdEqualTo(festivalId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 다른_축제의_id이면_false() {
            // given
            Long festivalId = 1L;
            Long otherFestivalId = 999L;
            Festival festival = FestivalFixture.create(festivalId);
            Question question = QuestionFixture.create(festival);

            // when
            boolean result = question.isFestivalIdEqualTo(otherFestivalId);

            // then
            assertThat(result).isFalse();
        }
    }
}
