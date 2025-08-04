package com.daedan.festabook.question.dto;

import com.daedan.festabook.question.domain.Question;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record QuestionSequenceUpdateResponses(
        @JsonValue List<QuestionSequenceUpdateResponse> responses
) {

    public static QuestionSequenceUpdateResponses from(List<Question> questions) {
        return new QuestionSequenceUpdateResponses(
                questions.stream()
                        .map(QuestionSequenceUpdateResponse::from)
                        .toList()
        );
    }
}
