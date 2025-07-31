package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;

public record QuestionAndAnswerUpdateResponse(
        Long questionId,
        String question,
        String answer
) {

    public static QuestionAndAnswerUpdateResponse from(Question question) {
        return new QuestionAndAnswerUpdateResponse(
                question.getId(),
                question.getQuestion(),
                question.getAnswer()
        );
    }
}
