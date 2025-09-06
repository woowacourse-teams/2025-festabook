package com.daedan.festabook.council.dto;

public class CouncilLoginRequestFixture {

    public static CouncilLoginRequest create(
            String username,
            String password
    ) {
        return new CouncilLoginRequest(
                username,
                password
        );
    }
}
