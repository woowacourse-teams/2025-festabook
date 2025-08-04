package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record QuestionResponses(
        @JsonValue List<QuestionResponse> responses
) {

    public static QuestionResponses from(List<Question> questions) {
        return new QuestionResponses(
                questions.stream()
                        .map(QuestionResponse::from)
                        .toList()
        );
    }
}
