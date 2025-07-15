package com.daedan.festabook.place.dto;

import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceImage;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record PlaceListResponses(
        @JsonValue List<PlaceListResponse> responses
) {

    public static PlaceListResponses from(List<Place> places, List<PlaceImage> placeImages) {
        return new PlaceListResponses(
                places.stream()
                        .map(place -> {
                            // TODO: 대표 이미지 추가 시 로직 변경
                            List<PlaceImage> placeImage = placeImages.stream()
                                    .filter(image -> image.getPlace().getId().equals(place.getId()))
                                    .toList();
                            return PlaceListResponse.from(place, placeImage.getFirst());
                        })
                        .toList()
        );
    }
}
