package com.daedan.festabook.question.service;

import com.daedan.festabook.question.domain.Question;
import com.daedan.festabook.question.dto.QuestionResponses;
import com.daedan.festabook.question.infrastructure.QuestionJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionJpaRepository questionJpaRepository;

    public QuestionResponses getAllQuestionByOrganizationId(Long organizationId) {
        List<Question> questions = questionJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
        return QuestionResponses.from(questions);
    }
}
