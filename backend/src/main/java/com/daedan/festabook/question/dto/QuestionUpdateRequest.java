package com.daedan.festabook.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuestionUpdateRequest(

        @Schema(description = "수정할 질문", example = "미소는 똑똑한가요?")
        String question,

        @Schema(description = "수정할 답변", example = "미소는 현명합니다.")
        String answer
) {
}
