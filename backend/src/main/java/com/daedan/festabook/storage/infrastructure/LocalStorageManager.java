package com.daedan.festabook.storage.infrastructure;

import com.daedan.festabook.storage.domain.StorageManager;
import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class LocalStorageManager implements StorageManager {

    private final String basePath;

    public LocalStorageManager(
            @Value("${local.storage.base-path}") String basePath
    ) {
        this.basePath = basePath;
    }

    @Override
    public StorageUploadResponse uploadFile(StorageUploadRequest request) {
        StoreFile storeFile = new StoreFile(request.file(), request.storagePath());

        try {
            Path fileStorePath = Paths.get(basePath, storeFile.getStoragePath());
            createDirectoryIfNotExists(fileStorePath.getParent());

            request.file().transferTo(fileStorePath);

            StorageUploadResponse response = new StorageUploadResponse(
                    ensureLeadingSlash(storeFile.getStoragePath()),
                    ensureLeadingSlash(storeFile.getStoragePath()),
                    request.storagePath()
            );
            return response;
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "로컬 저장소 파일 저장 실패 %s",
                    e.getMessage()
            ));
        }
    }

    private void createDirectoryIfNotExists(Path directoryPath) throws IOException {
        if (directoryPath != null && !Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            log.debug("디렉토리가 생성되었습니다: {}", directoryPath);
        }
    }

    private String ensureLeadingSlash(String storagePath) {
        if (storagePath.startsWith("/")) {
            return storagePath;
        }
        return String.format("/%s", storagePath);
    }
}
