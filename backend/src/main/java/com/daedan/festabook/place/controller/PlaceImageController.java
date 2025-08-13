package com.daedan.festabook.place.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places/{placeId}/images")
@Tag(name = "플레이스", description = "플레이스 관련 API")
public class PlaceImageController {

}
