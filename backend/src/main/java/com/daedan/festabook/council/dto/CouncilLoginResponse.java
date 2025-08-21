package com.daedan.festabook.council.dto;

public record CouncilLoginResponse(
        Long festivalId,
        String accessToken
) {

    public static CouncilLoginResponse from(Long festivalId, String accessToken) {
        return new CouncilLoginResponse(
                festivalId,
                accessToken
        );
    }
}
