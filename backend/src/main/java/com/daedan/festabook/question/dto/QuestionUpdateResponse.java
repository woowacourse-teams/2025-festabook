package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;

public record QuestionUpdateResponse(
        Long questionId,
        String question,
        String answer
) {

    public static QuestionUpdateResponse from(Question question) {
        return new QuestionUpdateResponse(
                question.getId(),
                question.getQuestion(),
                question.getAnswer()
        );
    }
}
