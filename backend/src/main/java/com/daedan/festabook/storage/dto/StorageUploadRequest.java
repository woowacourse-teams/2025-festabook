package com.daedan.festabook.storage.dto;

import org.springframework.web.multipart.MultipartFile;

public record StorageUploadRequest(
        MultipartFile file,
        String storagePath
) {
}
