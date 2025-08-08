package com.daedan.festabook.lostitem.dto;

import com.daedan.festabook.lostitem.domain.LostItem;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

public record LostItemResponses(
        @JsonValue List<LostItemResponse> responses
) {

    public static LostItemResponses from(List<LostItem> lostItems) {
        return new LostItemResponses(
                lostItems.stream()
                        .map(LostItemResponse::from)
                        .toList()
        );
    }
}
