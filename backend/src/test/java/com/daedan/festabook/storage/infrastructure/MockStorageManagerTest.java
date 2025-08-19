package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MockStorageManagerTest {

    private final MockStorageManager mockStorageManager = new MockStorageManager();

    @Nested
    class uploadFile {

        @Test
        void 성공() {
            // given
            String fileName = "test_123.png";
            String storagePath = "mock-path/test_123.png";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/png",
                    new byte[10]
            );
            StorageUploadRequest request = new StorageUploadRequest(mockFile, storagePath);

            // when
            StorageUploadResponse response = mockStorageManager.uploadFile(request);

            // then
            assertSoftly(s -> {
                s.assertThat(response.accessUrl()).isNotBlank();
                s.assertThat(response.storagePath()).isEqualTo(storagePath);
            });
        }
    }
}
