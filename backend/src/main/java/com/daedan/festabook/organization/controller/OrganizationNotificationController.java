package com.daedan.festabook.organization.controller;

import com.daedan.festabook.organization.dto.OrganizationNotificationRequest;
import com.daedan.festabook.organization.dto.OrganizationNotificationResponse;
import com.daedan.festabook.organization.service.OrganizationNotificationService;
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
@RequestMapping("/organizations")
@Tag(name = "조직 알림", description = "조직 알림 관련 API")
public class OrganizationNotificationController {

    private final OrganizationNotificationService organizationNotificationService;

    @PostMapping("/{organizationId}/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 조직의 알림 구독 (FCM 토픽 구독)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public OrganizationNotificationResponse subscribeOrganizationNotification(
            @PathVariable Long organizationId,
            @RequestBody OrganizationNotificationRequest request
    ) {
        return organizationNotificationService.subscribeOrganizationNotification(organizationId, request);
    }

    @DeleteMapping("/notifications/{organizationNotificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 조직의 알림 구독 취소 (FCM 토픽 구독 취소)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void unsubscribeOrganizationNotification(
            @PathVariable Long organizationNotificationId
    ) {
        organizationNotificationService.unsubscribeOrganizationNotification(organizationNotificationId);
    }
}
