package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;

public record QuestionResponse(
        Long id,
        String question,
        String answer,
        Integer sequence
) {

    public static QuestionResponse from(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getAnswer(),
                question.getSequence()
        );
    }
}
