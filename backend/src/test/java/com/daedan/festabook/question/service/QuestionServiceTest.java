package com.daedan.festabook.question.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
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
class QuestionServiceTest {

    @Mock
    private QuestionJpaRepository questionJpaRepository;

    @InjectMocks
    private QuestionService questionService;

    @Nested
    class getAllQuestionByOrganizationId {

        @Test
        void 성공_응답_개수() {
            // given
            int expected = 3;
            Long organizationId = 1L;
            List<Question> questions = QuestionFixture.createList(expected);
            given(questionJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId))
                    .willReturn(questions);

            // when
            QuestionResponses questionResponses = questionService.getAllQuestionByOrganizationId(
                    organizationId);

            // then
            int result = questionResponses.responses().size();
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 성공_내림차순_정렬() {
            // given
            Long organizationId = 1L;
            List<Question> questions = List.of(
                    QuestionFixture.create(LocalDateTime.now()),
                    QuestionFixture.create(LocalDateTime.now().minusDays(1))
            );
            given(questionJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId))
                    .willReturn(questions);

            // when
            QuestionResponses questionResponses = questionService.getAllQuestionByOrganizationId(organizationId);

            // then
            boolean result = questionResponses.responses().getFirst().createdAt()
                    .isAfter(questionResponses.responses().getLast().createdAt());
            assertThat(result).isTrue();
        }
    }
}
