package com.daedan.festabook.festival.controller;

import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationResponse;
import com.daedan.festabook.festival.service.FestivalNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals")
@Tag(name = "안드로이드 축제 공지사항 알림 구독", description = "안드로이드 축제 공지사항 알림 구독 API")
public class AndroidFestivalNotificationSubscriptionController {

    private final FestivalNotificationService festivalNotificationService;

    @PostMapping("/{festivalId}/notifications/android")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "안드로이드 장치 축제 공지사항 알림 구독 (FCM 토픽 구독)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public FestivalNotificationResponse subscribeFestivalNotification(
            @PathVariable Long festivalId,
            @RequestBody FestivalNotificationRequest request
    ) {
        return festivalNotificationService.subscribeAndroidFestivalNotification(festivalId, request);
    }
}
