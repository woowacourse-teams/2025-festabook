package com.daedan.festabook.question.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.question.domain.QuestionAnswer;
import com.daedan.festabook.question.domain.QuestionAnswerFixture;
import com.daedan.festabook.question.dto.QuestionAnswerResponses;
import com.daedan.festabook.question.infrastructure.QuestionAnswerJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QuestionAnswerServiceTest {

    @Mock
    private QuestionAnswerJpaRepository questionAnswerJpaRepository;

    @InjectMocks
    private QuestionAnswerService questionAnswerService;

    @Nested
    class getAllQuestionAnswerByOrganizationIdOrderByCreatedAtDesc {

        @Test
        void 성공_응답_개수() {
            // given
            int expected = 3;
            Long organizationId = 1L;
            List<QuestionAnswer> questionAnswers = QuestionAnswerFixture.createList(expected);
            given(questionAnswerJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId)).willReturn(
                    questionAnswers);

            // when
            int result = questionAnswerService.getAllQuestionAnswerByOrganizationIdOrderByCreatedAtDesc(organizationId)
                    .questionAnswerResponses().size();

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 성공_내림차순_정렬() {
            // given
            Long organizationId = 1L;
            List<QuestionAnswer> questionAnswers = List.of(
                    QuestionAnswerFixture.create(LocalDateTime.now()),
                    QuestionAnswerFixture.create(LocalDateTime.now().minusDays(1))
            );
            given(questionAnswerJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId))
                    .willReturn(questionAnswers);

            // when
            QuestionAnswerResponses questionAnswerResponses =
                    questionAnswerService.getAllQuestionAnswerByOrganizationIdOrderByCreatedAtDesc(organizationId);

            // then
            boolean result = questionAnswerResponses.questionAnswerResponses().getFirst().createdAt()
                    .isAfter(questionAnswerResponses.questionAnswerResponses().getLast().createdAt());
            assertThat(result).isTrue();
        }
    }
}
