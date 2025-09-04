package com.daedan.festabook.festival.domain;

import com.daedan.festabook.global.fixture.BaseEntityTestHelper;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FestivalImageFixture {

    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    private static final Integer DEFAULT_SEQUENCE = 3;

    public static FestivalImage create(
            Festival festival
    ) {
        return new FestivalImage(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
    }

    public static FestivalImage create(
            Festival festival,
            Long festivalImageId
    ) {
        FestivalImage festivalImage = new FestivalImage(
                festival,
                DEFAULT_IMAGE_URL,
                DEFAULT_SEQUENCE
        );
        BaseEntityTestHelper.setId(festivalImage, festivalImageId);
        return festivalImage;
    }

    public static FestivalImage create(
            Festival festival,
            Integer sequence
    ) {
        return new FestivalImage(
                festival,
                DEFAULT_IMAGE_URL,
                sequence
        );
    }

    public static FestivalImage create(
            Festival festival,
            Integer sequence,
            Long festivalImageId
    ) {
        FestivalImage festivalImage = new FestivalImage(
                festival,
                DEFAULT_IMAGE_URL,
                sequence
        );
        BaseEntityTestHelper.setId(festivalImage, festivalImageId);
        return festivalImage;
    }

    public static List<FestivalImage> createList(int size, Festival festival) {
        return IntStream.range(0, size)
                .mapToObj(i -> FestivalImageFixture.create(festival, i + 1))
                .collect(Collectors.toList());
    }
}
