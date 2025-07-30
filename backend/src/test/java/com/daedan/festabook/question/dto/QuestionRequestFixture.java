package com.daedan.festabook.question.dto;

public class QuestionRequestFixture {

    private static final String DEFAULT_QUESTION = "이 서비스는 무엇인가요?";
    private static final String DEFAULT_ANSWER = "이 서비스는 페스타북입니다.";

    public static QuestionRequest create() {
        return new QuestionRequest(
                DEFAULT_QUESTION,
                DEFAULT_ANSWER
        );
    }

    public static QuestionRequest create(
            String question,
            String answer
    ) {
        return new QuestionRequest(
                question,
                answer
        );
    }
}
