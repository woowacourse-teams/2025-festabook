package com.daedan.festabook.council.dto;

public class CouncilLoginRequestFixture {

    private static final String DEFAULT_USERNAME = "council";
    private static final String DEFAULT_PASSWORD = "1234";

    public static CouncilLoginRequest create(
            String username,
            String password
    ) {
        return new CouncilLoginRequest(
                username,
                password
        );
    }

    public static CouncilLoginRequest create() {
        return new CouncilLoginRequest(
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD
        );
    }
}
