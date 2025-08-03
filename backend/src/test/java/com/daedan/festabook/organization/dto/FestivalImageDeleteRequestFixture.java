package com.daedan.festabook.organization.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FestivalImageDeleteRequestFixture {

    public static FestivalImageDeleteRequest create(
            Long festivalImageId
    ) {
        return new FestivalImageDeleteRequest(
                festivalImageId
        );
    }

    public static List<FestivalImageDeleteRequest> createList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> create((long) (i + 1)))
                .collect(Collectors.toList());
    }
}
