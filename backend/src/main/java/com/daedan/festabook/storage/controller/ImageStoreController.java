package com.daedan.festabook.storage.controller;

import com.daedan.festabook.storage.dto.ImageUploadResponse;
import com.daedan.festabook.storage.service.ImageStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "이미지 저장", description = "이미지 저장 관련 API")
public class ImageStoreController {

    private final ImageStoreService imageStoreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이미지 파일 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    public ImageUploadResponse uploadImage(
            @RequestParam("image") MultipartFile file
    ) {
        return imageStoreService.uploadImage(file);
    }
}
