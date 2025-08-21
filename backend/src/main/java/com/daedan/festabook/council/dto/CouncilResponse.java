package com.daedan.festabook.council.dto;

import com.daedan.festabook.council.domain.Council;

public record CouncilResponse(
        Long festivalId,
        String username
) {

    public static CouncilResponse from(Council council) {
        return new CouncilResponse(
                council.getFestival().getId(),
                council.getUsername()
        );
    }
}
