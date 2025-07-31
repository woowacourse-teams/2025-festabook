package com.daedan.festabook.question.dto;

public record QuestionSequenceUpdateRequest(
        Long questionId,
        Integer sequence
) {
}
