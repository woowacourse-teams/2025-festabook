package com.daedan.festabook.council.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouncilUpdateRequest(

        @Schema(description = "현재 비밀번호", example = "1234")
        String currentPassword,

        @Schema(description = "새 비밀번호", example = "1234")
        String newPassword
) {
}
