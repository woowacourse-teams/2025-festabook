package com.daedan.festabook.place.controller;

import com.daedan.festabook.place.service.PlaceAnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
@Tag(name = "플레이스 공지", description = "플레이스 공지 관련 API")
public class PlaceAnnouncementController {

    private final PlaceAnnouncementService placeAnnouncementService;
}
