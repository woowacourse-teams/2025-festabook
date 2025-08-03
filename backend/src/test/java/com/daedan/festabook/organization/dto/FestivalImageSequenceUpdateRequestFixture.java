package com.daedan.festabook.organization.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FestivalImageSequenceUpdateRequestFixture {

    public static FestivalImageSequenceUpdateRequest create(
            Long festivalImageId,
            Integer sequence
    ) {
        return new FestivalImageSequenceUpdateRequest(
                festivalImageId,
                sequence
        );
    }

    public static List<FestivalImageSequenceUpdateRequest> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create((long) (i + 1), i + 1))
                .collect(Collectors.toList());
    }
}
