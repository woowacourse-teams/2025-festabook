package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;

public record QuestionSequenceUpdateResponse(
        Long questionId,
        Integer sequence
) {

    public static QuestionSequenceUpdateResponse from(Question question) {
        return new QuestionSequenceUpdateResponse(
                question.getId(),
                question.getSequence()
        );
    }
}
