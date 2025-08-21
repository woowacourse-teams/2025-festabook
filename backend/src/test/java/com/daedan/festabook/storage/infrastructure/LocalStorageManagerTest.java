package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.daedan.festabook.storage.dto.StorageUploadRequest;
import com.daedan.festabook.storage.dto.StorageUploadRequestFixture;
import com.daedan.festabook.storage.dto.StorageUploadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LocalStorageManagerTest {

    @TempDir
    private Path tempDirectory;

    private LocalStorageManager localStorageManager;

    @BeforeEach
    void setUp() {
        localStorageManager = new LocalStorageManager(tempDirectory.toString());
    }

    @Nested
    class uploadFile {

        @Test
        void 성공() {
            // given
            String fileName = "sample.png";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    fileName,
                    "image/png",
                    new byte[10]
            );
            String storagePath = "images/" + fileName;
            String expectedAccessUrl = "/" + storagePath;
            String expectedRelativePath = "/" + storagePath;
            StorageUploadRequest request = StorageUploadRequestFixture.create(mockFile, storagePath);

            // when
            StorageUploadResponse response = localStorageManager.uploadFile(request);

            // then
            assertSoftly(s -> {
                s.assertThat(response.accessUrl()).isEqualTo(expectedAccessUrl);
                s.assertThat(response.accessRelativePath()).startsWith(expectedRelativePath);
                s.assertThat(response.storagePath()).isEqualTo(storagePath);
            });
            Path expectedFilePath = tempDirectory.resolve(response.storagePath());
            assertThat(Files.exists(expectedFilePath)).isTrue();
        }

        @Test
        void 예외_파일_저장_실패시_RuntimeException_변환() throws IOException {
            // given
            // 파일 데이터를 비워서 IOException을 발생시킴
            MultipartFile mockFile = mock(MultipartFile.class);
            willThrow(IOException.class)
                    .given(mockFile)
                    .transferTo(any(Path.class));

            String storagePath = "images/sample.png";
            StorageUploadRequest request = new StorageUploadRequest(mockFile, storagePath);

            // when & then
            // RuntimeException이 발생하는지 검증
            assertThatThrownBy(() -> localStorageManager.uploadFile(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("로컬 저장소 파일 저장 실패");
        }
    }
}
