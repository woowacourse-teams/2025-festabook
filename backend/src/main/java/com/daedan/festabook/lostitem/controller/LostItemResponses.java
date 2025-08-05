package com.daedan.festabook.lostitem.controller;

import com.daedan.festabook.lostitem.domain.LostItem;
import java.util.List;

public record LostItemResponses(
        List<LostItemResponse> responses
) {

    public static LostItemResponses from(List<LostItem> lostItems) {
        return new LostItemResponses(
                lostItems.stream()
                        .map(LostItemResponse::from)
                        .toList()
        );
    }
}
