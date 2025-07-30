package com.daedan.festabook.question.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.dto.QuestionRequest;
import com.daedan.festabook.question.dto.QuestionResponse;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.dto.QuestionSequenceUpdateRequest;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionJpaRepository questionJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;

    public QuestionResponse createQuestion(Long organizationId, QuestionRequest request) {
        Organization organization = getOrganizationById(organizationId);

        Integer currentMaxSequence = questionJpaRepository.countByOrganizationId(organizationId);
        Integer nextSequence = currentMaxSequence + 1;

        Question question = new Question(organization, request.question(), request.answer(), nextSequence);
        Question savedQuestion = questionJpaRepository.save(question);

        return QuestionResponse.from(savedQuestion);
    }

    public QuestionResponses getAllQuestionByOrganizationId(Long organizationId) {
        List<Question> questions = questionJpaRepository.findByOrganizationIdOrderBySequenceDesc(organizationId);
        return QuestionResponses.from(questions);
    }

    @Transactional
    public QuestionResponse updateQuestionAndAnswer(Long questionId, QuestionRequest request) {
        Question question = getQuestionById(questionId);
        question.updateQuestionAndAnswer(request.question(), request.answer());
        return QuestionResponse.from(question);
    }

    @Transactional
    public QuestionResponses updateSequence(List<QuestionSequenceUpdateRequest> requests) {
        List<Question> questions = new ArrayList<>();

        for (QuestionSequenceUpdateRequest request : requests) {
            Question question = getQuestionById(request.id());
            question.updateSequence(request.sequence());
            questions.add(question);
        }

        return QuestionResponses.from(questions);
    }

    public void deleteQuestionByQuestionId(Long questionId) {
        questionJpaRepository.deleteById(questionId);
    }

    private Question getQuestionById(Long questionId) {
        return questionJpaRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 질문입니다.", HttpStatus.NOT_FOUND));
    }

    private Organization getOrganizationById(Long organizationId) {
        return organizationJpaRepository.findById(organizationId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 조직입니다.", HttpStatus.BAD_REQUEST));
    }
}
