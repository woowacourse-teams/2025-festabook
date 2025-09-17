package com.daedan.festabook.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FestivalLostItemGuideUpdateRequest(

        @Schema(description = "분실물 제보 및 수령 안내", example = "습득하신 분실물 또는 분실물 수령 문의는 구바우어관 301로 찾아와주세요.")
        String lostItemGuide
) {
}
