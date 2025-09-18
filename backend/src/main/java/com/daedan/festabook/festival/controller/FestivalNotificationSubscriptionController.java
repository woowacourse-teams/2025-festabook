package com.daedan.festabook.festival.controller;

import com.daedan.festabook.festival.dto.FestivalNotificationReadResponses;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationResponse;
import com.daedan.festabook.festival.service.FestivalNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals")
@Tag(name = "축제 공지사항 알림", description = "축제 공지사항 알림 관련 API")
public class FestivalNotificationSubscriptionController {

    private final FestivalNotificationService festivalNotificationService;

    @PostMapping("/{festivalId}/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제의 알림 구독 (FCM 토픽 구독) [old 안드로이드 알림 구독 버전]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public FestivalNotificationResponse subscribeFestivalNotification(
            @PathVariable Long festivalId,
            @RequestBody FestivalNotificationRequest request
    ) {
        return festivalNotificationService.subscribeFestivalNotification(festivalId, request);
    }

    @GetMapping("/notifications/{deviceId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 디바이스의 모든 축제 알림 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalNotificationReadResponses getAllFestivalNotificationByDeviceId(
            @PathVariable Long deviceId
    ) {
        return festivalNotificationService.getAllFestivalNotificationByDeviceId(deviceId);
    }

    @DeleteMapping("/notifications/{festivalNotificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 축제의 알림 구독 취소 (FCM 토픽 구독 취소)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void unsubscribeFestivalNotification(
            @PathVariable Long festivalNotificationId
    ) {
        festivalNotificationService.unsubscribeFestivalNotification(festivalNotificationId);
    }
}
