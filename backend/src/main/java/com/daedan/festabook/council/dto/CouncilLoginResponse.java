package com.daedan.festabook.council.dto;

public record CouncilLoginResponse(
        String accessToken
) {

    public static CouncilLoginResponse from(String accessToken) {
        return new CouncilLoginResponse(
                accessToken
        );
    }
}
