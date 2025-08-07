package com.daedan.festabook.lostitem.controller;

import com.daedan.festabook.global.argumentresolver.FestivalId;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemResponse;
import com.daedan.festabook.lostitem.dto.LostItemResponses;
import com.daedan.festabook.lostitem.dto.LostItemUpdateResponse;
import com.daedan.festabook.lostitem.service.LostItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/lost-items")
@Tag(name = "분실물", description = "분실물 관련 API")
class LostItemController {

    private final LostItemService lostItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "특정 축제의 분실물 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public LostItemResponse createLostItem(
            @Parameter(hidden = true) @FestivalId Long festivalId,
            @RequestBody LostItemRequest request
    ) {
        return lostItemService.createLostItem(festivalId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 축제의 모든 분실물 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public LostItemResponses getAllLostItemByFestivalId(
            @Parameter(hidden = true) @FestivalId Long festivalId
    ) {
        return lostItemService.getAllLostItemByFestivalId(festivalId);
    }

    @PatchMapping("/{lostItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "특정 분실물 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    public LostItemUpdateResponse updateLostItem(
            @PathVariable Long lostItemId,
            @RequestBody LostItemRequest request
    ) {
        return lostItemService.updateLostItem(lostItemId, request);
    }

    @DeleteMapping("/{lostItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "특정 분실물 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    public void deleteLostItemByLostItemId(
            @PathVariable Long lostItemId
    ) {
        lostItemService.deleteLostItemByLostItemId(lostItemId);
    }
}
