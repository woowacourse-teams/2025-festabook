package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.QuestionAnswer;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record QuestionAnswerResponses(
        @JsonValue List<QuestionAnswerResponse> responses
) {

    public static QuestionAnswerResponses from(List<QuestionAnswer> questionAnswers) {
        return new QuestionAnswerResponses(
                questionAnswers.stream()
                        .map(QuestionAnswerResponse::from)
                        .toList()
        );
    }
}
