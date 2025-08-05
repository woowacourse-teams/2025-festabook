package com.daedan.festabook.organization.controller;

import com.daedan.festabook.global.argumentresolver.OrganizationId;
import com.daedan.festabook.organization.dto.FestivalImageRequest;
import com.daedan.festabook.organization.dto.FestivalImageResponse;
import com.daedan.festabook.organization.dto.FestivalImageResponses;
import com.daedan.festabook.organization.dto.FestivalImageSequenceUpdateRequest;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.dto.OrganizationInformationResponse;
import com.daedan.festabook.organization.dto.OrganizationInformationUpdateRequest;
import com.daedan.festabook.organization.dto.OrganizationResponse;
import com.daedan.festabook.organization.service.FestivalImageService;
import com.daedan.festabook.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organizations")
@Tag(name = "조직", description = "조직 관련 API")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final FestivalImageService festivalImageService;

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 조직의 축제 이미지 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public FestivalImageResponse addFestivalImage(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody FestivalImageRequest request
    ) {
        return festivalImageService.addFestivalImage(organizationId, request);
    }

    @GetMapping("/geography")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 초기 지리 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public OrganizationGeographyResponse getOrganizationGeographyByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return organizationService.getOrganizationGeographyByOrganizationId(organizationId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 축제 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public OrganizationResponse getOrganizationByOrganizationId(
            @Parameter(hidden = true) @OrganizationId Long organizationId
    ) {
        return organizationService.getOrganizationByOrganizationId(organizationId);
    }

    @PatchMapping("/information")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 축제 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public OrganizationInformationResponse updateOrganizationInformation(
            @Parameter(hidden = true) @OrganizationId Long organizationId,
            @RequestBody OrganizationInformationUpdateRequest request
    ) {
        return organizationService.updateOrganizationInformation(organizationId, request);
    }

    @PatchMapping("/images/sequences")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 조직의 축제 이미지들 순서 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public FestivalImageResponses updateFestivalImagesSequence(
            @RequestBody List<FestivalImageSequenceUpdateRequest> requests
    ) {
        return festivalImageService.updateFestivalImagesSequence(requests);
    }

    @DeleteMapping("/images/{festivalImageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 조직의 축제 이미지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void removeFestivalImages(
            @PathVariable Long festivalImageId
    ) {
        festivalImageService.removeFestivalImage(festivalImageId);
    }
}
