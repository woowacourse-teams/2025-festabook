package com.daedan.festabook.festival.controller;

import com.daedan.festabook.festival.dto.FestivalCreateRequest;
import com.daedan.festabook.festival.dto.FestivalCreateResponse;
import com.daedan.festabook.festival.dto.FestivalGeographyResponse;
import com.daedan.festabook.festival.dto.FestivalImageRequest;
import com.daedan.festabook.festival.dto.FestivalImageResponse;
import com.daedan.festabook.festival.dto.FestivalImageResponses;
import com.daedan.festabook.festival.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalInformationResponse;
import com.daedan.festabook.festival.dto.FestivalInformationUpdateRequest;
import com.daedan.festabook.festival.dto.FestivalResponse;
import com.daedan.festabook.festival.dto.FestivalUniversityResponses;
import com.daedan.festabook.festival.service.FestivalImageService;
import com.daedan.festabook.festival.service.FestivalService;
import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.global.security.council.CouncilDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/festivals")
@Tag(name = "축제", description = "축제 관련 API")
public class FestivalController {

    private final FestivalService festivalService;
    private final FestivalImageService festivalImageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "축제 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalCreateResponse createFestival(
            @RequestBody FestivalCreateRequest request
    ) {
        return festivalService.createFestival(request);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제의 이미지 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public FestivalImageResponse addFestivalImage(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody FestivalImageRequest request
    ) {
        return festivalImageService.addFestivalImage(councilDetails.getFestivalId(), request);
    }

    @GetMapping("/geography")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 초기 지리 정보 조회", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalGeographyResponse getFestivalGeographyByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return festivalService.getFestivalGeographyByFestivalId(festivalId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 정보 조회", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalResponse getFestivalByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return festivalService.getFestivalByFestivalId(festivalId);
    }

    @GetMapping("/universities")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "대학 이름으로 축제 조회", security = @SecurityRequirement(name = "none"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalUniversityResponses getUniversitiesByUniversityName(
            @RequestParam String universityName
    ) {
        return festivalService.getUniversitiesByUniversityName(universityName);
    }

    @PatchMapping("/information")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalInformationResponse updateFestivalInformation(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody FestivalInformationUpdateRequest request
    ) {
        return festivalService.updateFestivalInformation(councilDetails.getFestivalId(), request);
    }

    @PatchMapping("/images/sequences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 이미지들 순서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalImageResponses updateFestivalImagesSequence(
            @AuthenticationPrincipal CouncilDetails councilDetails,
            @RequestBody List<FestivalImageSequenceUpdateRequest> requests
    ) {
        return festivalImageService.updateFestivalImagesSequence(councilDetails.getFestivalId(), requests);
    }

    @DeleteMapping("/images/{festivalImageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 축제의 이미지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void removeFestivalImage(
            @PathVariable Long festivalImageId,
            @AuthenticationPrincipal CouncilDetails councilDetails
    ) {
        festivalImageService.removeFestivalImage(councilDetails.getFestivalId(), festivalImageId);
    }
}
