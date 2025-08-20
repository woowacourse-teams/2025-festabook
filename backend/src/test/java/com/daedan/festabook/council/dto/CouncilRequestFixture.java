package com.daedan.festabook.council.dto;

public class CouncilRequestFixture {

    private static final Long DEFAULT_FESTIVAL_ID = 1L;
    private static final String DEFAULT_USERNAME = "council";
    private static final String DEFAULT_PASSWORD = "1234";

    public static CouncilRequest create() {
        return new CouncilRequest(
                DEFAULT_FESTIVAL_ID,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD
        );
    }

    public static CouncilRequest create(
            Long festivalId,
            String username,
            String password
    ) {
        return new CouncilRequest(
                festivalId,
                username,
                password
        );
    }
}
