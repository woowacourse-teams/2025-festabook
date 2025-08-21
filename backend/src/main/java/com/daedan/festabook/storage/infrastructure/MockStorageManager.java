package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod & !dev")
public class MockStorageManager implements StorageManager {

    private static final String MOCK_ACCESS_URL = "https://picsum.photos/200/300";

    @Override
    public StorageUploadResponse uploadFile(StorageUploadRequest request) {
        StoreFile storeFile = new StoreFile(request.file(), request.storagePath());

        StorageUploadResponse response = new StorageUploadResponse(
                MOCK_ACCESS_URL,
                ensureLeadingSlash(storeFile.getStoragePath()),
                storeFile.getStoragePath()
        );
        return response;
    }

    private String ensureLeadingSlash(String storagePath) {
        if (storagePath.startsWith("/")) {
            return storagePath;
        }
        return String.format("/%s", storagePath);
    }
}
