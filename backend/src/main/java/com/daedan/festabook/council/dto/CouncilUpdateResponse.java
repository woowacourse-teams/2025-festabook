package com.daedan.festabook.council.dto;

import com.daedan.festabook.council.domain.Council;

public record CouncilUpdateResponse(
        Long councilId,
        String username
) {

    public static CouncilUpdateResponse from(Council council) {
        return new CouncilUpdateResponse(
                council.getId(),
                council.getUsername()
        );
    }
}
