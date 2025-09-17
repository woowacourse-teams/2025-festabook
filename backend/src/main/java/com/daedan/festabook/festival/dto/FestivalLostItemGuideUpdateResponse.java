package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;

public record FestivalLostItemGuideUpdateResponse(
        String lostItemGuide
) {

    public static FestivalLostItemGuideUpdateResponse from(Festival festival) {
        return new FestivalLostItemGuideUpdateResponse(festival.getLostItemGuide());
    }
}
