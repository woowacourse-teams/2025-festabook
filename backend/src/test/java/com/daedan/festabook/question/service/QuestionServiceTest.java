package com.daedan.festabook.question.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.domain.QuestionFixture;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionRequestFixture;
import com.daedan.festabook.question.dto.QuestionResponse;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequestFixture;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateResponses;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
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
    private FestivalJpaRepository festivalJpaRepository;

    @InjectMocks
    private QuestionService questionService;

    @Nested
    class createQuestion {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            QuestionRequest request = QuestionRequestFixture.create(
                    "개도 데려갈 수 있나요?",
                    "죄송하지만 후유는 출입 금지입니다."
            );
            int questionCount = 1;

            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(questionJpaRepository.findMaxSequenceByFestivalId(festivalId))
                    .willReturn(Optional.of(questionCount));
            given(questionJpaRepository.save(any()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            QuestionResponse result = questionService.createQuestion(festivalId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.question()).isEqualTo(request.question());
                s.assertThat(result.answer()).isEqualTo(request.answer());
                s.assertThat(result.sequence()).isEqualTo(questionCount + 1);
            });
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;
            QuestionRequest request = QuestionRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> questionService.createQuestion(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getAllQuestionByFestivalId {

        @Test
        void 성공_응답_개수() {
            // given
            int expected = 3;
            Long festivalId = 1L;
            List<Question> questions = QuestionFixture.createList(expected);
            given(questionJpaRepository.findByFestivalIdOrderBySequenceAsc(festivalId))
                    .willReturn(questions);

            // when
            QuestionResponses questionResponses = questionService.getAllQuestionByFestivalId(festivalId);

            // then
            int result = questionResponses.responses().size();
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 성공_Sequence_오름차순_정렬() {
            // given
            Long festivalId = 1L;
            Question question2 = QuestionFixture.create(2);
            Question question1 = QuestionFixture.create(1);

            given(questionJpaRepository.findByFestivalIdOrderBySequenceAsc(festivalId))
                    .willReturn(List.of(question1, question2));

            // when
            QuestionResponses result = questionService.getAllQuestionByFestivalId(festivalId);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().getFirst().sequence()).isEqualTo(question1.getSequence());
                s.assertThat(result.responses().getLast().sequence()).isEqualTo(question2.getSequence());
            });
        }
    }

    @Nested
    class updateQuestionAndAnswer {

        @Test
        void 성공() {
            // given
            Long questionId = 1L;
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Question question = QuestionFixture.create(festival, questionId);

            given(questionJpaRepository.findById(questionId))
                    .willReturn(Optional.of(question));

            QuestionRequest request = QuestionRequestFixture.create(
                    "수정 후 제목입니다.",
                    "수정 후 답변입니다."
            );

            // when
            QuestionResponse result = questionService.updateQuestionAndAnswer(festivalId, questionId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.question()).isEqualTo(request.question());
                s.assertThat(result.answer()).isEqualTo(request.answer());
            });
        }

        @Test
        void 예외_존재하지_않는_질문() {
            // given
            Long festivalId = 1L;
            Long invalidQuestionId = 0L;
            QuestionRequest request = QuestionRequestFixture.create();

            // when & then
            assertThatThrownBy(() -> questionService.updateQuestionAndAnswer(festivalId, invalidQuestionId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 질문입니다.");
        }

        @Test
        void 예외_다른_축제의_질문일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Question question = QuestionFixture.create(requestFestival);

            given(questionJpaRepository.findById(question.getId()))
                    .willReturn(Optional.of(question));

            QuestionRequest request = QuestionRequestFixture.create();

            // when & then
            assertThatThrownBy(() ->
                    questionService.updateQuestionAndAnswer(otherFestival.getId(), question.getId(), request)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 질문이 아닙니다.");
        }
    }

    @Nested
    class updateSequence {

        @Test
        void 성공_수정_후에도_오름차순으로_재정렬() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);

            Long question1Id = 1L;
            Long question2Id = 2L;
            Long question3Id = 3L;

            Question question1 = QuestionFixture.create(festival, 1, question1Id);
            Question question2 = QuestionFixture.create(festival, 2, question2Id);
            Question question3 = QuestionFixture.create(festival, 3, question3Id);

            given(questionJpaRepository.findById(question1Id))
                    .willReturn(Optional.of(question1));
            given(questionJpaRepository.findById(question2Id))
                    .willReturn(Optional.of(question2));
            given(questionJpaRepository.findById(question3Id))
                    .willReturn(Optional.of(question3));

            QuestionSequenceUpdateRequest request1 = QuestionSequenceUpdateRequestFixture.create(question1Id, 2);
            QuestionSequenceUpdateRequest request2 = QuestionSequenceUpdateRequestFixture.create(question2Id, 3);
            QuestionSequenceUpdateRequest request3 = QuestionSequenceUpdateRequestFixture.create(question3Id, 1);
            List<QuestionSequenceUpdateRequest> requests = List.of(request1, request2, request3);

            // when
            QuestionSequenceUpdateResponses result = questionService.updateSequence(festival.getId(), requests);

            // then
            assertSoftly(s -> {
                s.assertThat(result.responses().get(0).questionId()).isEqualTo(question3Id);
                s.assertThat(result.responses().get(0).sequence()).isEqualTo(1);

                s.assertThat(result.responses().get(1).questionId()).isEqualTo(question1Id);
                s.assertThat(result.responses().get(1).sequence()).isEqualTo(2);

                s.assertThat(result.responses().get(2).questionId()).isEqualTo(question2Id);
                s.assertThat(result.responses().get(2).sequence()).isEqualTo(3);
            });
        }

        @Test
        void 예외_존재하지_않는_질문() {
            // given
            Long festivalId = 1L;
            List<QuestionSequenceUpdateRequest> requests = QuestionSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> questionService.updateSequence(festivalId, requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 질문입니다.");
        }

        @Test
        void 예외_다른_축제의_질문일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Long questionId = 1L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Question question = QuestionFixture.create(requestFestival, questionId);

            given(questionJpaRepository.findById(question.getId()))
                    .willReturn(Optional.of(question));

            List<QuestionSequenceUpdateRequest> requests = QuestionSequenceUpdateRequestFixture.createList(3);

            // when & then
            assertThatThrownBy(() -> questionService.updateSequence(otherFestival.getId(), requests))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 질문이 아닙니다.");
        }
    }

    @Nested
    class deleteQuestionByQuestionId {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Long questionId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            Question question = QuestionFixture.create(festival, questionId);

            given(questionJpaRepository.findById(questionId))
                    .willReturn(Optional.of(question));

            // when
            questionService.deleteQuestionByQuestionId(festival.getId(), question.getId());

            // then
            then(questionJpaRepository).should()
                    .deleteById(questionId);
        }

        @Test
        void 예외_다른_축제의_질문일_경우() {
            // given
            Long requestFestivalId = 1L;
            Long otherFestivalId = 999L;
            Festival requestFestival = FestivalFixture.create(requestFestivalId);
            Festival otherFestival = FestivalFixture.create(otherFestivalId);
            Question question = QuestionFixture.create(requestFestival);

            given(questionJpaRepository.findById(question.getId()))
                    .willReturn(Optional.of(question));

            // when & then
            assertThatThrownBy(() ->
                    questionService.deleteQuestionByQuestionId(otherFestival.getId(), question.getId())
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("해당 축제의 질문이 아닙니다.");
        }
    }
}
