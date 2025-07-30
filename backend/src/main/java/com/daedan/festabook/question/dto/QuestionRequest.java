package com.daedan.festabook.question.dto;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.question.domain.Question;
import io.swagger.v3.oas.annotations.media.Schema;

public record QuestionRequest(

        @Schema(description = "질문", example = "개도 데려갈 수 있나요?")
        String question,

        @Schema(description = "답변", example = "죄송하지만 후유는 출입 금지입니다.")
        String answer
) {

    public Question toQuestion(Organization organization, Integer sequence) {
        return new Question(
                organization,
                question,
                answer,
                sequence
        );
    }
}
