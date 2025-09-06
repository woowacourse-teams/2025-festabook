package com.daedan.festabook.council.dto;

public class CouncilRequestFixture {

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
