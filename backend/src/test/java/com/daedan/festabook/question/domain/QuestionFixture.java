package com.daedan.festabook.question.domain;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionFixture {

    private static final Organization DEFAULT_ORGANIZATION = OrganizationFixture.create();
    private static final String DEFAULT_QUESTION = "이 서비스는 무엇인가요?";
    private static final String DEFAULT_ANSWER = "이 서비스는 페스타북입니다.";
    private static final Integer DEFAULT_SEQUENCE = 3;
    private static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();

    public static Question create(
            Organization organization,
            String question,
            String answer,
            Integer sequence,
            LocalDateTime createdAt
    ) {
        return new Question(
                organization,
                question,
                answer,
                sequence,
                createdAt
        );
    }

    public static Question create(
            Organization organization,
            String question,
            String answer,
            Integer sequence
    ) {
        return new Question(
                organization,
                question,
                answer,
                sequence,
                DEFAULT_CREATED_AT
        );
    }

    public static Question create(
            Organization organization,
            LocalDateTime createdAt
    ) {
        return create(
                organization,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE,
                createdAt
        );
    }

    public static Question create(
            LocalDateTime createdAt
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE,
                createdAt
        );
    }

    public static Question create(
            Organization organization
    ) {
        return create(
                organization,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE,
                DEFAULT_CREATED_AT
        );
    }

    public static Question create(
            Organization organization,
            Integer sequence
    ) {
        return create(
                organization,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                sequence,
                DEFAULT_CREATED_AT
        );
    }

    public static Question createWithQuestion(
            String question
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                question,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE,
                DEFAULT_CREATED_AT
        );
    }

    public static Question createWithAnswer(
            String answer
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_QUESTION,
                answer,
                DEFAULT_SEQUENCE,
                DEFAULT_CREATED_AT
        );
    }

    public static Question create(
            String question,
            String answer,
            Integer sequence
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                question,
                answer,
                sequence,
                DEFAULT_CREATED_AT
        );
    }

    public static Question create(
            Integer sequence
    ) {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                sequence,
                DEFAULT_CREATED_AT
        );
    }

    public static Question create() {
        return create(
                DEFAULT_ORGANIZATION,
                DEFAULT_QUESTION,
                DEFAULT_ANSWER,
                DEFAULT_SEQUENCE,
                DEFAULT_CREATED_AT
        );
    }

    public static List<Question> createList(List<LocalDateTime> createdAts, Organization organization) {
        return createdAts.stream()
                .map(createdAt -> create(organization, createdAt))
                .collect(Collectors.toList());
    }

    public static List<Question> createList(int size, Organization organization) {
        return IntStream.range(0, size)
                .mapToObj(i -> create(organization))
                .collect(Collectors.toList());
    }

    public static List<Question> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create())
                .collect(Collectors.toList());
    }
}
