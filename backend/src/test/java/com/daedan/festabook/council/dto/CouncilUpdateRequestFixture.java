package com.daedan.festabook.council.dto;

public class CouncilUpdateRequestFixture {

    public static CouncilUpdateRequest create(
            String currentPassword,
            String newPassword
    ) {
        return new CouncilUpdateRequest(
                currentPassword,
                newPassword
        );
    }
}
