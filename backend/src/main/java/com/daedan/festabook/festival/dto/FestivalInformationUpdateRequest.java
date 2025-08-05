package com.daedan.festabook.festival.dto;

import java.time.LocalDate;

public record FestivalInformationUpdateRequest(
        String festivalName,
        LocalDate startDate,
        LocalDate endDate
) {
}
