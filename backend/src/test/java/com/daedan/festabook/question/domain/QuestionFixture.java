package com.daedan.festabook.question.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_QUESTION = "이 서비스는 무엇인가요?";
    private static final String DEFAULT_ANSWER = "이 서비스는 페스타북입니다.";
    private static final Integer DEFAULT_SEQUENCE = 3;

    public static Question create(
            Festival festival
    ) {
        return new Question(
                festival,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE
        );
    }

    public static Question create(
            Long questionId,
            Festival festival
    ) {
        Question question = new Question(
                festival,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE
        );
        BaseEntityTestHelper.setId(question, questionId);
        return question;
    }

    public static Question create(
            Long questionId,
            Festival festival,
            Integer sequence
    ) {
        Question question = new Question(
                festival,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                sequence
        );
        BaseEntityTestHelper.setId(question, questionId);
        return question;
    }

    public static Question create(
            Festival festival,
            Integer sequence
    ) {
        return new Question(
                festival,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                sequence
        );
    }

    public static Question create(
            Integer sequence
    ) {
        return new Question(
                DEFAULT_FESTIVAL,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                sequence
        );
    }

    public static Question create() {
        return new Question(
                DEFAULT_FESTIVAL,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE
        );
    }

    public static Question createWithQuestion(
            String question
    ) {
        return new Question(
                DEFAULT_FESTIVAL,
                question,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE
        );
    }

    public static Question createWithAnswer(
            String answer
    ) {
        return new Question(
                DEFAULT_FESTIVAL,
                DEFAULT_QUESTION,
                answer,
                DEFAULT_SEQUENCE
        );
    }

    public static List<Question> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(festival))
                .collect(Collectors.toList());
    }

    public static List<Question> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create())
                .collect(Collectors.toList());
    }
}
