package com.daedan.festabook.question.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestionSequenceUpdateRequestFixture {

    public static QuestionSequenceUpdateRequest create(
            Long questionId,
            Integer sequence
    ) {
        return new QuestionSequenceUpdateRequest(
                questionId,
                sequence
        );
    }

    public static List<QuestionSequenceUpdateRequest> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create((long) (i + 1), i + 1))
                .collect(Collectors.toList());
    }
}
