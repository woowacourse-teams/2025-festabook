package com.daedan.festabook.question.dto;

public class QuestionUpdateRequestFixture {

    private static final String DEFAULT_QUESTION = "이 서비스는 무엇인가요?";
    private static final String DEFAULT_ANSWER = "이 서비스는 페스타북입니다.";

    public static QuestionUpdateRequest create() {
        return new QuestionUpdateRequest(
                DEFAULT_QUESTION,
                DEFAULT_ANSWER
        );
    }

    public static QuestionUpdateRequest create(
            String question,
            String answer
    ) {
        return new QuestionUpdateRequest(
                question,
                answer
        );
    }
}
