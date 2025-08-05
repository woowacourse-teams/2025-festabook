package com.daedan.festabook.organization.dto;

public record FestivalImageSequenceUpdateRequest(
        Long festivalImageId,
        Integer sequence
) {
}
