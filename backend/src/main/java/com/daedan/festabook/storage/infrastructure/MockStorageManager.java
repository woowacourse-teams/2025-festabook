package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class MockStorageManager implements StorageManager {

    private static final String MOCK_ACCESS_URL = "https://picsum.photos/200/300";

    @Override
    public StorageUploadResponse uploadFile(StorageUploadRequest request) {
        StoreFile storeFile = new StoreFile(request.file(), request.storagePath());
        return new StorageUploadResponse(MOCK_ACCESS_URL, storeFile.getStoragePath());
    }
}
