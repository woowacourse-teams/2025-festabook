package com.daedan.festabook.question.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QuestionAnswerTest {

    private final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private final String DEFAULT_QUESTION = "질문 내용입니다.";
    private final String DEFAULT_ANSWER = "답변 내용입니다.";
    private final LocalDateTime NOW = LocalDateTime.now();

    @Nested
    class validateOrganization {

        @Test
        void 성공() {
            // given
            Organization organization = OrganizationFixture.create();

            // when & then
            assertThatCode(() -> new QuestionAnswer(organization, DEFAULT_QUESTION, DEFAULT_ANSWER, NOW))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_조직_null() {
            // given
            Organization organization = null;

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(organization, DEFAULT_QUESTION, DEFAULT_ANSWER, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Organization은 null일 수 없습니다.");
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
            assertThatCode(() -> new QuestionAnswer(DEFAULT_ORGANIZATION, question, DEFAULT_ANSWER, NOW))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_질문_null() {
            // given
            String question = null;

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, question, DEFAULT_ANSWER, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("질문은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_질문_blank() {
            // given
            String question = " ";

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, question, DEFAULT_ANSWER, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("질문은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_질문_길이_초과() {
            // given
            int maxQuestionLength = 500;
            String question = "미".repeat(maxQuestionLength + 1);

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, question, DEFAULT_ANSWER, NOW)
            )
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
            assertThatCode(() -> new QuestionAnswer(DEFAULT_ORGANIZATION, DEFAULT_QUESTION, answer, NOW))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_답변_null() {
            // given
            String answer = null;

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, DEFAULT_QUESTION, answer, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_답변_blank() {
            // given
            String answer = " ";

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, DEFAULT_QUESTION, answer, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 비어 있을 수 없습니다.");
        }

        @Test
        void 예외_답변_길이_초과() {
            // given
            int maxAnswerLength = 1000;
            String answer = "미".repeat(maxAnswerLength + 1);

            // when & then
            assertThatThrownBy(() ->
                    new QuestionAnswer(DEFAULT_ORGANIZATION, DEFAULT_QUESTION, answer, NOW)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("답변은 1000자를 초과할 수 없습니다.");
        }
    }
}
