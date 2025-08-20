package com.daedan.festabook.storage.service;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.ImageUploadResponse;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageStoreService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private final StorageManager storageManager;

    @Value("${storage.image.max-size}")
    private long maxImageSize;

    @Value("${storage.image.path-prefix}")
    private String imagePathPrefix;

    public ImageUploadResponse uploadImage(MultipartFile file) {
        validateFile(file);
        validateImageSize(file);
        validateImageType(file);

        String storagePath = generateUniqueFilePath(file.getOriginalFilename());
        StorageUploadRequest request = new StorageUploadRequest(file, storagePath);

        StorageUploadResponse response = storageManager.uploadFile(request);
        return new ImageUploadResponse(response.accessUrl());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일은 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateImageSize(MultipartFile file) {
        if (file.getSize() > maxImageSize) {
            throw new BusinessException(
                    String.format("이미지 크기는 %d 바이트를 초과할 수 없습니다.", maxImageSize),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateImageType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new BusinessException("Content-Type이 빈 파일은 업로드 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(
                    String.format("이미지 파일만 업로드할 수 있습니다. 업로드 파일 타입: %s", contentType),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private String generateUniqueFilePath(String originalFilename) {
        String uniqueFilename = generateUniqueFilename(originalFilename);
        return String.format("%s/%s", imagePathPrefix, uniqueFilename);
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException("파일 확장자가 없는 파일은 업로드 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return String.format("%s.%s", UUID.randomUUID(), extension);
    }
}
