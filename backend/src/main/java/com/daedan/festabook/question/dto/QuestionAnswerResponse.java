package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.QuestionAnswer;
import java.time.LocalDateTime;

public record QuestionAnswerResponse(
        Long id,
        Long organizationId,
        String title,
        String question,
        String answer,
        LocalDateTime createdAt
) {

    public static QuestionAnswerResponse from(QuestionAnswer questionAnswer) {
        return new QuestionAnswerResponse(
                questionAnswer.getId(),
                questionAnswer.getOrganization().getId(),
                questionAnswer.getTitle(),
                questionAnswer.getQuestion(),
                questionAnswer.getAnswer(),
                questionAnswer.getCreatedAt()
        );
    }
}
