package com.daedan.festabook.council.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouncilRequest(

        @Schema(description = "축제의 ID", example = "1")
        Long festivalId,

        @Schema(description = "아이디", example = "council")
        String username,

        @Schema(description = "비밀번호", example = "1234")
        String password
) {
}
