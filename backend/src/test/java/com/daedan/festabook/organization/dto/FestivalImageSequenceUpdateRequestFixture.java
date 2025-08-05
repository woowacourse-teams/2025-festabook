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
        return IntStream.range(1, size + 1)
                .mapToObj(i -> create((long) i, i))
                .collect(Collectors.toList());
    }
}
