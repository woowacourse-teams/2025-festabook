package com.daedan.festabook.question.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionAnswerFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final String DEFAULT_TITLE = "자주 묻는 질문";
    private static final String DEFAULT_QUESTION = "이 서비스는 무엇인가요?";
    private static final String DEFAULT_ANSWER = "이 서비스는 페스타북입니다.";
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();

    public static QuestionAnswer create(
            Organization organization,
            String title,
            String question,
            String answer,
            LocalDateTime createdAt
    ) {
        return new QuestionAnswer(
                organization,
                title,
                question,
                answer,
                createdAt
        );
    }

    public static QuestionAnswer create(
            Organization organization,
            LocalDateTime createdAt
    ) {
        return create(
                organization,
                DEFAULT_TITLE,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                createdAt
        );
    }

    public static QuestionAnswer create(
            LocalDateTime createdAt
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_TITLE,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                createdAt
        );
    }

    public static QuestionAnswer create() {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_TITLE,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_CREATED_AT
        );
    }

    public static List<QuestionAnswer> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create())
                .collect(Collectors.toList());
    }
}
