package com.daedan.festabook.council.dto;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.global.security.role.RoleType;
import java.util.Set;

public record CouncilResponse(
        Long councilId,
        String username,
        Set<RoleType> roleTypes
) {

    public static CouncilResponse from(Council council) {
        return new CouncilResponse(
                council.getId(),
                council.getUsername(),
                council.getRoles()
        );
    }
}
