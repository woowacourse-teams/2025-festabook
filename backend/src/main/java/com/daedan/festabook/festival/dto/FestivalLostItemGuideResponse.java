package com.daedan.festabook.festival.dto;

import com.daedan.festabook.festival.domain.Festival;

public record FestivalLostItemGuideResponse(

        String lostItemGuide
) {

    public static FestivalLostItemGuideResponse from(Festival festival) {
        return new FestivalLostItemGuideResponse(festival.getLostItemGuide());
    }
}
