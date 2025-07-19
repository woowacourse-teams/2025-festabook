package com.daedan.festabook.organization.controller;

import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;
import com.daedan.festabook.organization.dto.OrganizationBookmarkResponse;
import com.daedan.festabook.organization.service.OrganizationBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations/bookmarks")
@Tag(name = "조직 북마크", description = "조직 북마크 관련 API")
public class OrganizationBookmarkController {

    private final OrganizationBookmarkService organizationBookmarkService;

    @PostMapping("/{organizationId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 조직의 북마크 생성 (+ FCM 토픽 구독)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public OrganizationBookmarkResponse createOrganizationBookmark(
            @PathVariable Long organizationId,
            @RequestBody OrganizationBookmarkRequest request
    ) {
        return organizationBookmarkService.createOrganizationBookmark(organizationId, request);
    }

    @DeleteMapping("/{organizationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 조직의 북마크 삭제 (+ FCM 토픽 구독 취소)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteOrganizationBookmark(
            @PathVariable Long organizationId,
            @RequestBody OrganizationBookmarkRequest request
    ) {
        organizationBookmarkService.deleteOrganizationBookmark(organizationId, request);
    }
}
