package com.daedan.festabook.question.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionRequestFixture;
import com.daedan.festabook.question.dto.QuestionResponse;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequestFixture;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private QuestionService questionService;

    @Nested
    class createQuestion {

        @Test
        void 성공_이전_질문이_없을_경우_첫번째_sequence로_저장() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            QuestionRequest request = QuestionRequestFixture.create(
                    "개도 데려갈 수 있나요?",
                    "죄송하지만 후유는 출입 금지입니다."
            );
            int sequence = 1;

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(questionJpaRepository.findTopByOrganizationIdOrderBySequenceDesc(organizationId))
                    .willReturn(Optional.empty());
            given(questionJpaRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            QuestionResponse result = questionService.createQuestion(organizationId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.question()).isEqualTo(request.question());
                s.assertThat(result.answer()).isEqualTo(request.answer());
                s.assertThat(result.sequence()).isEqualTo(sequence);
            });
        }

        @Test
        void 성공_이전_질문이_있을_경우_다음_sequence로_저장() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);
            QuestionRequest request = QuestionRequestFixture.create(
                    "개도 데려갈 수 있나요?",
                    "죄송하지만 후유는 출입 금지입니다."
            );
            Question latestQuestion = QuestionFixture.create(organization, "이전 질문", "이전 답변", 3);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));
            given(questionJpaRepository.findTopByOrganizationIdOrderBySequenceDesc(organizationId))
                    .willReturn(Optional.of(latestQuestion));
            given(questionJpaRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            QuestionResponse result = questionService.createQuestion(organizationId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.question()).isEqualTo(request.question());
                s.assertThat(result.answer()).isEqualTo(request.answer());
                s.assertThat(result.sequence()).isEqualTo(latestQuestion.getSequence() + 1);
            });
        }

        @Test
        void 예외_존재하지_않는_조직() {
            // given
            Long invalidOrganizationId = 0L;
            QuestionRequest request = QuestionRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> questionService.createQuestion(invalidOrganizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

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

    @Nested
    class updateQuestionAndAnswer {

        @Test
        void 성공() {
            // given
            Long questionId = 1L;
            Question question = QuestionFixture.create();

            given(questionJpaRepository.findById(questionId))
                    .willReturn(Optional.of(question));

            QuestionRequest request = QuestionRequestFixture.create(
                    "수정 후 제목입니다.",
                    "수정 후 답변입니다."
            );

            // when
            QuestionResponse result = questionService.updateQuestionAndAnswer(questionId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.question()).isEqualTo(request.question());
                s.assertThat(result.answer()).isEqualTo(request.answer());
            });
        }

        @Test
        void 예외_존재하지_않는_질문() {
            // given
            Long invalidQuestionId = 0L;
            QuestionRequest request = QuestionRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> questionService.updateQuestionAndAnswer(invalidQuestionId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 질문입니다.");
        }
    }

    @Nested
    class updateSequence {

        @Test
        void 성공() {
            // given
            Long question1Id = 1L;
            Long question2Id = 2L;
            Long question3Id = 3L;

            Question question1 = QuestionFixture.create(1);
            Question question2 = QuestionFixture.create(2);
            Question question3 = QuestionFixture.create(3);

            given(questionJpaRepository.findById(question1Id))
                    .willReturn(Optional.of(question1));
            given(questionJpaRepository.findById(question2Id))
                    .willReturn(Optional.of(question2));
            given(questionJpaRepository.findById(question3Id))
                    .willReturn(Optional.of(question3));

            int changedQuestion1Sequence = 2;
            int changedQuestion2Sequence = 3;
            int changedQuestion3Sequence = 1;

            QuestionSequenceUpdateRequest request1 = QuestionSequenceUpdateRequestFixture.create(
                    question1Id,
                    changedQuestion1Sequence
            );
            QuestionSequenceUpdateRequest request2 = QuestionSequenceUpdateRequestFixture.create(
                    question2Id,
                    changedQuestion2Sequence
            );
            QuestionSequenceUpdateRequest request3 = QuestionSequenceUpdateRequestFixture.create(
                    question3Id,
                    changedQuestion3Sequence
            );
            List<QuestionSequenceUpdateRequest> requests = List.of(request1, request2, request3);

            // when
            QuestionResponses result = questionService.updateSequence(requests);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).sequence()).isEqualTo(changedQuestion1Sequence);
                s.assertThat(result.responses().get(1).sequence()).isEqualTo(changedQuestion2Sequence);
                s.assertThat(result.responses().get(2).sequence()).isEqualTo(changedQuestion3Sequence);
            });
        }

        @Test
        void 예외_존재하지_않는_질문() {
            // given
            List<QuestionSequenceUpdateRequest> requests = QuestionSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> questionService.updateSequence(requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 질문입니다.");
        }
    }

    @Nested
    class deleteQuestionByQuestionId {

        @Test
        void 성공() {
            // given
            Long questionId = 1L;

            willDoNothing().given(questionJpaRepository).deleteById(questionId);

            // when
            questionService.deleteQuestionByQuestionId(questionId);

            // then
            then(questionJpaRepository).should()
                    .deleteById(questionId);
        }
    }
}
