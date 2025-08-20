package com.daedan.festabook.storage.dto;

import org.springframework.web.multipart.MultipartFile;

public class StorageUploadRequestFixture {

    public static StorageUploadRequest create(
            MultipartFile file,
            String storagePath
    ) {
        return new StorageUploadRequest(
                file,
                storagePath
        );
    }
}
