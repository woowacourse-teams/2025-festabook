package com.daedan.festabook.question.service;

import com.daedan.festabook.question.dto.QuestionAnswerResponses;
import com.daedan.festabook.question.infrastructure.QuestionAnswerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionAnswerService {

    private final QuestionAnswerJpaRepository questionAnswerJpaRepository;

    public QuestionAnswerResponses getAllQuestionAnswerByOrganizationIdOrderByCreatedAtDesc(Long organizationId) {
        return QuestionAnswerResponses.from(
                questionAnswerJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId));
    }
}
