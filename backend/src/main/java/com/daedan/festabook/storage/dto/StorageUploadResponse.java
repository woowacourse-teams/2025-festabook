package com.daedan.festabook.storage.dto;

public record StorageUploadResponse(
        String accessUrl,
        String accessRelativePath,
        String storagePath
) {
}
