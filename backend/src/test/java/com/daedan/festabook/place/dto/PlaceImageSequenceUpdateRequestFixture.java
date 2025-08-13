package com.daedan.festabook.place.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlaceImageSequenceUpdateRequestFixture {

    public static PlaceImageSequenceUpdateRequest create(
            Long placeImageId,
            int sequence
    ) {
        return new PlaceImageSequenceUpdateRequest(
                placeImageId,
                sequence
        );
    }

    public static List<PlaceImageSequenceUpdateRequest> createList(int size) {
        return IntStream.range(1, size + 1)
                .mapToObj(i -> create((long) i, i))
                .collect(Collectors.toList());
    }
}
