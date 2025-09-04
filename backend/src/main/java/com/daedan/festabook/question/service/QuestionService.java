package com.daedan.festabook.question.service;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionResponse;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateResponses;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionJpaRepository questionJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;

    @Transactional
    public QuestionResponse createQuestion(Long festivalId, QuestionRequest request) {
        Festival festival = getFestivalById(festivalId);

        Integer currentMaxSequence = questionJpaRepository.findMaxSequenceByFestivalId(festivalId)
                .orElseGet(() -> 0);
        Integer newSequence = currentMaxSequence + 1;

        Question question = new Question(festival, request.question(), request.answer(), newSequence);
        Question savedQuestion = questionJpaRepository.save(question);

        return QuestionResponse.from(savedQuestion);
    }

    public QuestionResponses getAllQuestionByFestivalId(Long festivalId) {
        List<Question> questions = questionJpaRepository.findByFestivalIdOrderBySequenceAsc(festivalId);
        return QuestionResponses.from(questions);
    }

    @Transactional
    public QuestionResponse updateQuestionAndAnswer(Long festivalId, Long questionId, QuestionRequest request) {
        Question question = getQuestionById(questionId);
        validateQuestionBelongsToFestival(question, festivalId);

        question.updateQuestionAndAnswer(request.question(), request.answer());
        return QuestionResponse.from(question);
    }

    @Transactional
    public QuestionSequenceUpdateResponses updateSequence(
            Long festivalId,
            List<QuestionSequenceUpdateRequest> requests
    ) {
        List<Question> questions = new ArrayList<>();

        for (QuestionSequenceUpdateRequest request : requests) {
            Question question = getQuestionById(request.questionId());
            validateQuestionBelongsToFestival(question, festivalId);
            question.updateSequence(request.sequence());
            questions.add(question);
        }

        Collections.sort(questions);

        return QuestionSequenceUpdateResponses.from(questions);
    }

    public void deleteQuestionByQuestionId(Long festivalId, Long questionId) {
        Question question = getQuestionById(questionId);
        validateQuestionBelongsToFestival(question, festivalId);

        questionJpaRepository.deleteById(questionId);
    }

    private Question getQuestionById(Long questionId) {
        return questionJpaRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 질문입니다.", HttpStatus.NOT_FOUND));
    }

    private Festival getFestivalById(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }

    private void validateQuestionBelongsToFestival(Question question, Long festivalId) {
        if (!question.isFestivalIdEqualTo(festivalId)) {
            throw new BusinessException("해당 축제의 질문이 아닙니다.", HttpStatus.FORBIDDEN);
        }
    }
}
