package com.daedan.festabook.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuestionSequenceUpdateRequest(

        @Schema(description = "FAQ ID", example = "1")
        Long questionId,

        @Schema(description = "FAQ 순서", example = "1")
        Integer sequence
) {
}
